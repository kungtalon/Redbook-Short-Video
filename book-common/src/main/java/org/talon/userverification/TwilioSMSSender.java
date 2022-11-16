package org.talon.userverification;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Zelong
 * On 2022/10/29
 **/

@Component
public class TwilioSMSSender implements IAuthCodeSender {

    @Autowired
    private TwilioConfig twilioConfig;

    public void send(String msg, String userPhone) {
         Message.creator(
            new PhoneNumber(userPhone), // to
            new PhoneNumber(twilioConfig.getDefaultSender()), // from
            msg
        ).create();
    }
}
