package fr.leswebdevs.mail;

import fr.leswebdevs.MailConnectionCredentials;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator {

    private final MailConnectionCredentials credentials;

    public MailAuthenticator(MailConnectionCredentials credentials) {
        this.credentials = credentials;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(credentials.getEmail(), credentials.getPassword());
    }
}