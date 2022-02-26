package org.elefteria.elefteriasn.email;

public interface EmailSender {
    void send(String to, String emailText, String subject);
}
