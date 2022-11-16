package org.talon.userverification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Zelong
 * On 2022/10/29
 **/
@Service
public class VerificationService {

    @Autowired
    private EmailSender emailSender;

    public void sendVerificationInfo(String email, String code) {
        String body = String.format("<p>Hello!</p>" +
                "<p>You are registering for RedBook. Your verification code is <b>" + code + "</b></p>" +
                "<p>Your code will expire in five minutes.</p>");
        emailSender.send(body, email);
    }
}
