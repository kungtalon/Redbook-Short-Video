package org.talon.userverification;

import com.twilio.Twilio;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Created by Zelong
 * On 2022/10/29
 **/

@Configuration
@Data
public class TwilioConfig {
    @Value("${twilio.account-sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token}")
    private String twilioAuthToken;

    @Value("${twilio.default-sender}")
    private String defaultSender;

    @PostConstruct
    public void init() {
        Twilio.init(
                getTwilioAccountSid(),
                getTwilioAuthToken()
        );
    }
}
