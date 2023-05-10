package fr.leswebdevs.controller;

import fr.leswebdevs.ConfigFile;
import fr.leswebdevs.MailConnectionCredentials;
import fr.leswebdevs.MailManager;
import fr.leswebdevs.MainApp;
import jakarta.mail.MessagingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ConfigConnection implements Initializable {

    private static final String PORT_REGEX = "^[0-9]{0,4}$";
    private static final String MESSAGE_CONNECTION_ERROR = "Erreur lors de la connexion";
    private static final String MESSAGE_FILE_ERROR = "Erreur lors de l'enregistrement de la configuration";
    private static final String COLOR_RED = "#FF0000";
    private static final String COLOR_Black = "#000000";
    @FXML
    public AnchorPane configAnchorPane;
    @FXML
    public TextInputControl configEmail;
    @FXML
    public TextInputControl configPassword;
    @FXML
    public TextInputControl configAddrIMAP;
    @FXML
    public TextInputControl configPortIMAP;
    @FXML
    public TextInputControl configAddrSMTP;
    @FXML
    public TextInputControl configPortSMTP;
    @FXML
    public CheckBox configSSL;
    @FXML
    public Label configMessageError;
    private MailConnectionCredentials credentials;

    public void onClickValidate() throws MessagingException {
        setMessageError("Connexion...", true, COLOR_Black);
        MailManager mail = tryToConnect();
        mail.connect().whenComplete((message, ex) -> {
            if (ex != null) {
                setMessageError(MESSAGE_CONNECTION_ERROR, true, COLOR_RED);
            } else {
                try {
                    writeFile();
                    showNextScene();
                } catch (Exception e) {
                    setMessageError(e.getMessage(), true, COLOR_RED);
                }
            }
        });
    }

    private void writeFile() throws Exception {
        if (!ConfigFile.writeFile(credentials)) {
            throw new Exception(MESSAGE_FILE_ERROR);
        }
    }

    private MailManager tryToConnect() {
        credentials = MailConnectionCredentials.builder()
            .email(configEmail.getText())
            .password(configPassword.getText())
            .smtpHost(configAddrSMTP.getText())
            .smtpPort(Integer.parseInt(configPortSMTP.getText()))
            .imapHost(configAddrIMAP.getText())
            .imapPort(Integer.parseInt(configPortIMAP.getText()))
            .isSSL(configSSL.isSelected())
            .build();
        MailManager mailManager = new MailManager();
        mailManager.setCredentials(credentials);
        return mailManager;
    }

    private void showNextScene() {
        Platform.runLater(() -> {
            Stage stage = (Stage) configAnchorPane.getScene().getWindow();
            URL location = getClass().getResource("/fxml/main.fxml");
            Scene scene = null;
            try {
                scene = new Scene(new FXMLLoader(location).load(), MainApp.SCREEN_WIDTH, MainApp.SCREEN_HEIGHT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setScene(scene);
            stage.show();
        });
    }

    private void setMessageError(String message, boolean isVisible, String hexColor) {
        Platform.runLater(() -> {
            configMessageError.setText(message);
            configMessageError.setTextFill(Color.web(hexColor));
            configMessageError.setVisible(isVisible);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTextFormatters();
    }

    private void setTextFormatters() {
        configPortSMTP.setTextFormatter(formatInput(PORT_REGEX));
        configPortIMAP.setTextFormatter(formatInput(PORT_REGEX));
    }

    private TextFormatter<Boolean> formatInput(String regex) {
        return new TextFormatter<>(change ->
            (change.getControlNewText().matches(regex)) ? change : null);
    }
}
