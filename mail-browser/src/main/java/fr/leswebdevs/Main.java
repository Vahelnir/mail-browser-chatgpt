package fr.leswebdevs;

import fr.leswebdevs.mail.MailConnectionCredentials;
import fr.leswebdevs.mail.MailManager;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.event.MessageCountListener;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws MessagingException, ExecutionException, InterruptedException {
        MailConnectionCredentials credentials = MailConnectionCredentials.builder()
            .imapHost(System.getenv("IMAP_HOST"))
            .imapPort(Integer.parseInt(System.getenv("IMAP_PORT")))
            .smtpHost(System.getenv("SMTP_HOST"))
            .smtpPort(Integer.parseInt(System.getenv("SMTP_PORT")))
            .email(System.getenv("EMAIL"))
            .password(System.getenv("PASSWORD"))
            .isSSL(System.getenv("IS_SSL").equals("true"))
            .build();
        MailManager mailManager = new MailManager();
        mailManager.connect(credentials).get();

        Store store = mailManager.getReadStore();

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