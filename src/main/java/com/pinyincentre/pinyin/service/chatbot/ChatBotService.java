package com.pinyincentre.pinyin.service.chatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatBotService {
    private static final Logger logger = LoggerFactory.getLogger(ChatBotService.class);

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent}")
    private String geminiBaseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ChatBotService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private String getGeminiApiUrl() {
        return geminiBaseUrl + "?key=" + geminiApiKey;
    }

    private String getPinyinCentreContext() {
        return """
            Bạn là trợ lý ảo của Trung tâm tiếng Trung Pinyin Centre. Dưới đây là thông tin về trung tâm:
            
            Thông tin cơ bản:
            - Tên: Pinyin Centre - Trung tâm tiếng Trung
            - Website: https://www.pinyincentre.com
            - Chuyên dạy tiếng Trung cho người Việt Nam
            - Có các khóa học online và offline
                Bạn là chatbot của Pinyin Centre, trung tâm học tiếng Trung uy tín, hỗ trợ học viên đạt chứng chỉ HSK. Dưới đây là thông tin về trung tâm và lộ trình học HSK:
                - **Lộ trình học HSK và giá cả**: \s
                  - **HSK 1**: Người mới bắt đầu, 150 từ vựng cơ bản, ngữ pháp đơn giản, tập trung nghe và đọc, 2-3 tháng (30-45 giờ học), giá: 1.890.000 VNĐ. \s
                  - **HSK 2**: Giao tiếp cơ bản hàng ngày, 300 từ vựng (bao gồm HSK 1), 3-4 tháng (45-60 giờ học), giá: 1.690.000 VNĐ. \s
                  - **HSK 3**: 600 từ vựng, giao tiếp quen thuộc (mua sắm, ăn uống, du lịch, công việc), đọc viết đoạn văn đơn giản, 5-6 tháng (75-90 giờ học), giá: 2.190.000 VNĐ. \s
                  - **HSK 4**: 1.200 từ vựng, giao tiếp tự nhiên, ngữ pháp phức tạp hơn, 6-8 tháng (90-120 giờ học), giá: 3.690.000 VNĐ. \s
                  - **Combo HSK 1.2**: 2.190.000 VNĐ (36 buổi, 3 buổi/tuần). \s
                  - **Combo HSK 1.2.3**: 3.990.000 VNĐ (56-60 buổi, 3 buổi/tuần, giảm giá HSK so với học riêng). \s
                  - **Combo HSK 2.3**: 3.290.000 VNĐ (40-45 buổi, 3 buổi/tuần, giảm giá HSK so với học riêng).
                - **Thông tin liên hệ**: \s
                  - Số điện thoại: 0369960429. \s
                  - Facebook: https://www.facebook.com/profile.php?id=61560228721942. \s
                - **Giảng viên**: \s
                  - Đội ngũ giảng viên hàng đầu, bao gồm: \s
                    - Trần Thị Bình: Tốt nghiệp loại giỏi, 6 năm kinh nghiệm giảng dạy tiếng Trung, đạt HSK 6, HSKK cao cấp. \s
                    - Lưu Quỳnh Chi: Trình độ HSK 5+, 5 năm kinh nghiệm giảng dạy tiếng Trung và tiếng Nhật. \s
                    - Hoàng Đức Bình: 3 năm kinh nghiệm giảng dạy, đạt HSK 6, HSKK cao cấp, tốt nghiệp tiếng Trung tại giang hồ. \s
                Hãy trả lời câu hỏi của người dùng dựa trên thông tin này, giữ giọng điệu chuyên nghiệp, thân thiện. Nếu người dùng hỏi ngoài lộ trình, hãy trả lời ngắn gọn và lịch sự, đồng thời cung cấp thông tin liên hệ nếu phù hợp.
            """;
    }

    public String getGeminiResponse(String userPrompt) {
        String fullPrompt = getPinyinCentreContext() + "\n\nCâu hỏi của học viên: " + userPrompt;

        try {
            // Create request body
            ObjectNode requestBody = createRequestBody(fullPrompt);

            // Configure headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

            // Send request to Gemini API
            ResponseEntity<String> response = restTemplate.exchange(
                    getGeminiApiUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return extractResponseText(response.getBody());

        } catch (HttpStatusCodeException e) {
            logger.error("Gemini API error: Status code: {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 429) {
                return "Xin lỗi, hệ thống đang tạm thời quá tải. Vui lòng thử lại sau ít phút.";
            }
            return "Có lỗi xảy ra khi xử lý yêu cầu của bạn. Vui lòng thử lại sau.";

        } catch (Exception e) {
            logger.error("Error processing Gemini API request", e);
            return "Có lỗi xảy ra trong hệ thống. Vui lòng thử lại sau.";
        }
    }

    private ObjectNode createRequestBody(String prompt) {
        ObjectNode requestBody = objectMapper.createObjectNode();
        ObjectNode contentNode = objectMapper.createObjectNode();
        contentNode.put("text", prompt);

        ArrayNode partsArray = objectMapper.createArrayNode();
        partsArray.add(contentNode);

        ArrayNode contentsArray = objectMapper.createArrayNode();
        contentsArray.add(objectMapper.createObjectNode().set("parts", partsArray));

        requestBody.set("contents", contentsArray);
        return requestBody;
    }

    private String extractResponseText(String responseBody) {
        try {
            ObjectNode jsonResponse = objectMapper.readTree(responseBody).deepCopy();
            return jsonResponse
                    .get("candidates")
                    .get(0)
                    .get("content")
                    .get("parts")
                    .get(0)
                    .get("text")
                    .asText();
        } catch (Exception e) {
            logger.error("Error parsing Gemini API response", e);
            throw new RuntimeException("Failed to parse API response", e);
        }
    }
}
