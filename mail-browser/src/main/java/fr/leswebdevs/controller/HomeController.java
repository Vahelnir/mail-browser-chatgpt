package fr.leswebdevs.controller;

import fr.leswebdevs.MainApp;
import fr.leswebdevs.adapter.MailTableItemAdapter;
import fr.leswebdevs.dto.MailTableItem;
import fr.leswebdevs.mail.MailManager;
import jakarta.mail.*;
import jakarta.mail.event.ConnectionEvent;
import jakarta.mail.event.ConnectionListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Pagination;
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

    private final ObservableList<MailTableItem> mails = FXCollections.observableArrayList();
    @FXML
    public TableView<MailTableItem> mailTableView;
    @FXML
    public Pagination mailPagination;
    private MailManager mailManager;
    private Folder currentFolder;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MainApp mainApp = MainApp.getInstance();

        SortedList<MailTableItem> sortedMailItems = new SortedList<>(
            mails,
            (a, b) -> b.getReceivedAt()
                .compareTo(a.getReceivedAt())
        );
        mailTableView.setItems(sortedMailItems);

        mainApp.getMailManager()
            .thenCompose((mailManager) -> {
                this.mailManager = mailManager;
                return openInbox();
            })
            .thenApply(this::setFolder)
            .thenApply(this::getMessageCount)
            .thenAccept((messageCount) -> {
                loadMessages(messageCount - MAIL_PER_PAGE + 1, messageCount);
                initPagination(messageCount);
            })
            .whenComplete((s, error) -> {
                System.out.println(error);
                error.printStackTrace();
            });
    }

    private void initPagination(int messageCount) {
        Platform.runLater(this::updatePage);

        mailPagination
            .currentPageIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
                int from = messageCount - ((newValue.intValue() + 1) * MAIL_PER_PAGE);
                loadMessages(from + 1, from + MAIL_PER_PAGE);
            });
    }

    private void updatePage() {
        try {
            mailPagination.setPageCount(currentFolder.getMessageCount() / MAIL_PER_PAGE);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private Folder setFolder(Folder folder) {
        currentFolder = folder;
        return folder;
    }


    private void loadMessages(int from, int to) {
        try {
            Message[] messages = currentFolder.getMessages(from, to);

            FetchProfile fp = new FetchProfile();
            fp.add(Item.ENVELOPE);
            fp.add(UIDFolder.FetchProfileItem.FLAGS);
            fp.add(UIDFolder.FetchProfileItem.CONTENT_INFO);
            currentFolder.fetch(messages, fp);

            List<MailTableItem> mailTableItems = Arrays.stream(messages)
                .map(message -> {
                    try {
                        return MailTableItemAdapter.from(message);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

            mails.setAll(mailTableItems);
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
