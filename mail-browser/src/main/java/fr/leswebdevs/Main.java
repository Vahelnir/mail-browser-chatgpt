package fr.leswebdevs;

import jakarta.mail.*;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.event.MessageCountListener;

import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imap");
        props.put("mail.imap.auth", "true");
        props.put("mail.imap.starttls.enable", "true");
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.host", "imap.gmail.com");
        props.put("mail.imap.port", "993");

        String email = System.getenv("EMAIL");
        String imapPassword = System.getenv("IMAP_PASSWORD");

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, imapPassword);
            }
        };
        Session session = Session.getInstance(props, authenticator);

        Store store = session.getStore();
        store.connect();

        Folder folder = store.getFolder("INBOX");
        // on n'a pas besoin de modifier quoi que ce soit pour le moment
        folder.open(Folder.READ_ONLY);
        // regarde si un message est changé (lu ou pas)
        folder.addMessageChangedListener((messageChangedEvent) -> {
            Message message = messageChangedEvent.getMessage();
            try {
                System.out.println(message.getSubject() + " | " + message.getContent());
            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        // regarde si on ajoute ou supprime un message (receive
        folder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent e) {
                System.out.println("message count add event");
            }

            @Override
            public void messagesRemoved(MessageCountEvent e) {
                System.out.println(e.getMessages());
                System.out.println("message count remove event");

            }
        });

        System.out.println("start !");

        while (folder.isOpen()) {
        }

        System.out.println("stop !");
    }
}