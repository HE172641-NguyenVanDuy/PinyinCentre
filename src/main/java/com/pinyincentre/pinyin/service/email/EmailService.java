package com.pinyincentre.pinyin.service.email;


import com.pinyincentre.pinyin.entity.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailService {

    @Value("${spring.sendgrid.from-email}")
    private String from;

    @Autowired
    private final SendGrid sendGrid;


    public void send(String to, String subject, String text)  {
        Email fromEmail = new Email(from);
        Email endEmail = new Email(to);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(fromEmail,subject,endEmail,content);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = new SendGrid(from).api(request);
            if(response.getStatusCode() == 202) {
                log.info("Email sent successfully");
            } else {
                log.error("Email sent failed");
            }

        } catch (IOException e) {
            log.error("Error occurred while sending email, error: {}" , e.getMessage());
            //throw new RuntimeException(e);
        }
    }

    public void emailVerification(String to, User user) throws IOException  {
        log.info("Email verification started");
        Email fromEmail = new Email(from);
        Email endEmail = new Email(to);
        String subject = "Xác thực tài khoản";

        String verificationLink = "h";
        // ĐiDdihj nghĩa template
        Map<String, String> map = new HashMap<>();
        //map.put("verificationlink", verificationLink);
        map.put("password", user.getPassword());

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(endEmail);

        map.forEach(personalization::addDynamicTemplateData);

        mail.addPersonalization(personalization);
        mail.setTemplateId("d-8bbd9ac2499a4e95ab10ad66f5aa1561");

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sendGrid.api(request);
        if(response.getStatusCode() == 202) {
            log.info("Verification sent successfully");

        } else {
            log.error("Verification sent failed");
        }
    }
}
