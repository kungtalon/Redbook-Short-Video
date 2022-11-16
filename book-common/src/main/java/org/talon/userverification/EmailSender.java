package org.talon.userverification;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Zelong
 * On 2022/10/29
 **/

@Slf4j
@Component
public class EmailSender implements IAuthCodeSender{

    private final String myEmail;
    private final String authCode;
    private final Session session;

    public EmailSender(@Value("${mail.address}") String myEmail,
                       @Value("${mail.auth-code}") String authCode,
                       @Value("${mail.smtp}") String smtpEmail) {
        this.myEmail = myEmail;
        this.authCode = authCode;
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", smtpEmail);
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.transport.protocol", "smtp");
        session = Session.getInstance(props);
    }

    private MimeMessage createEmail(Session session, String sendMail, String receiveMail, String msg) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sendMail, "RedBook", "UTF-8"));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "User", "UTF-8"));
        message.setSubject("Verify your registration for RedBook!", "UTF-8");
        message.setContent(msg, "text/html;charset=UTF-8");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    @Override
    public void send(String msg, String targetEmail) {
        if (session == null) {
            log.error("No session is initialized for email sender!");
        }
        try {
            assert session != null;
            try (Transport transport = session.getTransport()) {
                transport.connect(myEmail, authCode);
                MimeMessage message = createEmail(session, myEmail, targetEmail, msg);
                transport.sendMessage(message, message.getAllRecipients());
            }
        } catch (Exception e) {
            log.error("Error sending verification code via email..." + e);
        }
    }
}
