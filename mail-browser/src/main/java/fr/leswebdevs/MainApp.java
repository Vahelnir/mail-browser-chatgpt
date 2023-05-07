package fr.leswebdevs;

import jakarta.mail.MessagingException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MainApp extends Application {

    private static MainApp instance;
    private CompletableFuture<MailManager> mailManager;

    public static void main(String[] args) {
        launch();
    }

    public static MainApp getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
//        try {
//            initMailManager();
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
        URL location = getClass().getResource("/fxml/main.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        Scene scene = new Scene(loader.load(), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    private CompletableFuture<MailManager> initMailManager() {
        MailConnectionCredentials credentials = MailConnectionCredentials.builder()
                .imapHost(System.getenv("IMAP_HOST"))
                .imapPort(Integer.parseInt(System.getenv("IMAP_PORT")))
                .smtpHost(System.getenv("SMTP_HOST"))
                .smtpPort(Integer.parseInt(System.getenv("SMTP_PORT")))
                .email(System.getenv("EMAIL"))
                .password(System.getenv("PASSWORD"))
                .isSSL(System.getenv("IS_SSL").equals("true"))
                .build();
        MailManager mailManager = new MailManager(credentials);
        try {
            return mailManager.connect();
        } catch (MessagingException e) {
            throw new CompletionException(e);
        }
    }

    public CompletableFuture<MailManager> getMailManager() {
        if (mailManager == null) {
            mailManager = initMailManager();
        }
        return mailManager;
    }

}
