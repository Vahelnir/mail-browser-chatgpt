package fr.leswebdevs;

import fr.leswebdevs.mail.MailAuthenticator;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

import java.util.Properties;

public class MailManager {
    private Store readStore;
    private Session writeSession;

    public MailManager() {
    }

    public void connect(MailConnectionCredentials credentials) throws MessagingException {
        readStore = createReadSession(credentials);
        writeSession = createWriteSession(credentials);
    }

    private Store createReadSession(MailConnectionCredentials credentials) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imap");
        props.put("mail.imap.auth", "true");
        props.put("mail.imap.ssl.enable", Boolean.toString(credentials.isSSL()));
        props.put("mail.imap.host", credentials.getImapHost());
        props.put("mail.imap.port", credentials.getImapPort());

        Authenticator authenticator = new MailAuthenticator(credentials);

        Session session = Session.getInstance(props, authenticator);

        Store store = session.getStore();
        store.connect();

        return store;
    }

    private Session createWriteSession(MailConnectionCredentials credentials) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.socketFactory.port", credentials.getSmtpPort()); //SSL Port
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class

        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", Boolean.toString(credentials.isSSL())); //enable STARTTLS


        Authenticator authenticator = new MailAuthenticator(credentials);

        return Session.getInstance(props, authenticator);
    }

    public Store getReadStore() {
        return readStore;
    }

    public Session getWriteSession() {
        return writeSession;
    }
}
