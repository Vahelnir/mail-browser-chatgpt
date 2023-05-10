package fr.leswebdevs.controller;

import fr.leswebdevs.MailManager;
import fr.leswebdevs.MainApp;
import fr.leswebdevs.adapter.MailTableItemAdapter;
import fr.leswebdevs.dto.MailTableItem;
import jakarta.mail.*;
import jakarta.mail.event.ConnectionEvent;
import jakarta.mail.event.ConnectionListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static jakarta.mail.FetchProfile.Item;

public class HomeController implements Initializable {

    private static final int MAIL_PER_PAGE = 20;

    private final ObservableList<Message> mails = FXCollections.observableArrayList();
    @FXML
    public TableView<Message> mailTableView;
    private MailManager mailManager;
    private Folder currentFolder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MainApp mainApp = MainApp.getInstance();
        mailTableView.setItems(mails);
        mainApp.getMailManager()
            .thenCompose((mailManager) -> {
                this.mailManager = mailManager;
                return openInbox();
            })
            .thenApply(this::setFolder)
            .thenApply(this::getMessageCount)
            .thenAccept((messageCount) -> {
                loadMessages(messageCount - MAIL_PER_PAGE + 1, messageCount);
            })
            .whenComplete((s, error) -> {
                // TODO: handle error
                System.out.println(error);
            });
    }

    private Folder setFolder(Folder folder) {
        currentFolder = folder;
        return folder;
    }

    private void loadMessages(int from, int to) {
        try {
            Message[] messages = currentFolder.getMessages(from, to);
            mails.setAll(messages);
            System.out.println("updating messages");
        } catch (MessagingException e) {
            throw new RuntimeException("Cannot load messages from " + from + " to " + to);
        }
    }

    private int getMessageCount(Folder currentFolder) {
        try {
            return currentFolder.getMessageCount();
        } catch (MessagingException e) {
            throw new RuntimeException("Cannot get message count");
        }
    }

    private CompletableFuture<Folder> openInbox() {
        return CompletableFuture
            .supplyAsync(() -> {
                Store store = mailManager.getReadStore();
                try {
                    return store.getFolder("INBOX");
                } catch (MessagingException e) {
                    throw new CompletionException(e);
                }
            })
            .thenCompose((folder) -> {
                CompletableFuture<Folder> folderCompletableFuture = new CompletableFuture<>();
                folder.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void opened(ConnectionEvent e) {
                        folderCompletableFuture.complete(folder);
                    }

                    @Override
                    public void disconnected(ConnectionEvent e) {
                    }

                    @Override
                    public void closed(ConnectionEvent e) {
                    }
                });

                // on n'a pas besoin de modifier quoi que ce soit pour le moment
                try {
                    folder.open(Folder.READ_ONLY);
                } catch (MessagingException e) {
                    throw new CompletionException(e);
                }
                return folderCompletableFuture;
            });
    }

}
