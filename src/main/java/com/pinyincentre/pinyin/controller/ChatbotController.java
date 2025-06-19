package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.service.chatbot.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "https://www.pinyincentre.com")
@RequestMapping("/api/gemini")
public class ChatbotController {

    @Autowired
    private ChatBotService geminiService;

    @PostMapping(value = "/generate-response", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> generateResponse(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String response = geminiService.getGeminiResponse(prompt);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("response", response);
        return responseMap;
    }
}
