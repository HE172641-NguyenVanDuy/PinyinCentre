package com.pinyincentre.pinyin.service.email;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
}
