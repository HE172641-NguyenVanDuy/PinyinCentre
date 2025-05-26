//package com.pinyincentre.pinyin.controller;
//
//import com.pinyincentre.pinyin.service.email.EmailService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j(topic = "EMAIL-Controller")
//public class EmailController {
//
//    private final EmailService emailService;
//
//    @GetMapping("/send-email")
//    public void send(@RequestParam String to,@RequestParam String subject,@RequestParam String content) {
//        log.info("Sending email to {}", to);
//        emailService.send(to, subject, content);
//        log.info("Email sent");
//    }
//}
