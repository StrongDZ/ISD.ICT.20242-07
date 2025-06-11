package com.example.aims.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.aims.service.user.EmailService;
import com.example.aims.dto.EmailRequest;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-CONTROLLER")
public class EmailController {
private final EmailService emailService;

@PostMapping("/send")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request) {
log.info("Sending email to {}", request.getTo());
try {
emailService.send(request.getTo(), request.getSubject(), request.getBody());
return ResponseEntity.ok().body("Email sent successfully");
} catch (Exception e) {
log.error("Failed to send email: {}", e.getMessage());
return ResponseEntity.badRequest().body("Failed to send email: " +
e.getMessage());
}
}
}