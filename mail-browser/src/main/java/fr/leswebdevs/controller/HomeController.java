package fr.leswebdevs.controller;

import fr.leswebdevs.MailManager;
import fr.leswebdevs.MainApp;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import jakarta.mail.event.ConnectionEvent;
import jakarta.mail.event.ConnectionListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class HomeController implements Initializable {

    private final ObservableList<Message> mails = FXCollections.observableArrayList();
    @FXML
    public TableView<Message> mailTableView;
    private MailManager mailManager;
    private Folder currentFolder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mailManager = MainApp.getInstance().getMailManager();
        mailTableView.setItems(mails);
        try {
            openInbox(this::onFolderReady);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void onFolderReady(Folder folder, ConnectionEvent event) {
        currentFolder = folder;
        int count = getMessageCount();

        loadMessages(count - 20, count);
    }

    private void loadMessages(int from, int to) {
        try {
            Message[] messages = currentFolder.getMessages(from, to);
            Message message = messages[0];
            System.out.println("nbr de messages" + messages.length);
            mails.setAll(messages);
        } catch (MessagingException e) {
            throw new RuntimeException("Cannot load messages from " + from + " to " + to);
        }
    }

    private int getMessageCount() {
        try {
            return currentFolder.getMessageCount();
        } catch (MessagingException e) {
            throw new RuntimeException("Cannot get message count");
        }
    }

    private void openInbox(BiConsumer<Folder, ConnectionEvent> folderConsumer) throws MessagingException {
        Store store = mailManager.getReadStore();

        Folder folder = store.getFolder("INBOX");

        folder.addConnectionListener(new ConnectionListener() {
            @Override
            public void opened(ConnectionEvent e) {
                folderConsumer.accept(folder, e);
            }

            @Override
            public void disconnected(ConnectionEvent e) {
            }

            @Override
            public void closed(ConnectionEvent e) {
            }
        });

        // on n'a pas besoin de modifier quoi que ce soit pour le moment
        folder.open(Folder.READ_ONLY);
    }

}
