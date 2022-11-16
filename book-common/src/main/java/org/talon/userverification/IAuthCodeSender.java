package org.talon.userverification;

public interface IAuthCodeSender {

    void send(String msg, String target);
}
