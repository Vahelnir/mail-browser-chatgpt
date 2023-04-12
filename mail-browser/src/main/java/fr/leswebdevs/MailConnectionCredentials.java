package fr.leswebdevs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MailConnectionCredentials {
    private String imapHost;
    private int imapPort;
    private String smtpHost;
    private int smtpPort;
    private String email;
    private String password;
    private boolean isSSL;
}
