package fr.leswebdevs;

import fr.leswebdevs.mail.MailAuthenticator;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MailManager {

    private MailConnectionCredentials credentials;
    private Store readStore;
    private Session writeSession;

    public MailManager() {
    }

    public MailManager(MailConnectionCredentials credentials) {
        this.credentials = credentials;
    }

    public CompletableFuture<MailManager> connect() throws MessagingException {
        return connect(credentials);
    }

    public CompletableFuture<MailManager> connect(MailConnectionCredentials credentials) {
        return CompletableFuture.allOf(
                        createReadSession(credentials)
                                .thenAccept((store) -> {
                                    readStore = store;
                                    System.out.println("set read store");
                                }),
                        createWriteSession(credentials)
                                .thenAccept((session) -> {
                                    writeSession = session;
                                })
                )
                .thenApply((e) -> this);
    }

    private CompletableFuture<Store> createReadSession(MailConnectionCredentials credentials) {
        return CompletableFuture.supplyAsync(() -> {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imap");
            props.put("mail.imap.auth", "true");
            props.put("mail.imap.ssl.enable", Boolean.toString(credentials.isSSL()));
            props.put("mail.imap.host", credentials.getImapHost());
            props.put("mail.imap.port", credentials.getImapPort());

            Authenticator authenticator = new MailAuthenticator(credentials);

            Session session = Session.getInstance(props, authenticator);

            Store store = null;
            try {
                store = session.getStore();
                store.connect();
            } catch (MessagingException e) {
                throw new CompletionException(e);
            }

            return store;
        });
    }

    private CompletableFuture<Session> createWriteSession(MailConnectionCredentials credentials) {
        return CompletableFuture.supplyAsync(() -> {
            Properties props = new Properties();
            props.put("mail.smtp.socketFactory.port", credentials.getSmtpPort()); //SSL Port
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class

            props.put("mail.smtp.port", "587"); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", Boolean.toString(credentials.isSSL())); //enable STARTTLS


            Authenticator authenticator = new MailAuthenticator(credentials);
            return Session.getInstance(props, authenticator);
        });
    }

    public MailConnectionCredentials getCredentials() {
        return credentials;
    }

    public void setCredentials(MailConnectionCredentials credentials) {
        this.credentials = credentials;
    }

    public Store getReadStore() {
        System.out.println("get read store");
        return readStore;
    }

    public Session getWriteSession() {
        return writeSession;
    }
}
