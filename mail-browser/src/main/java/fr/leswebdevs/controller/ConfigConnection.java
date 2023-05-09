package fr.leswebdevs.controller;

import fr.leswebdevs.ConfigFile;
import fr.leswebdevs.MailConnectionCredentials;
import fr.leswebdevs.MailManager;
import fr.leswebdevs.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ConfigConnection implements Initializable {

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

    private static final String PORT_REGEX = "^[0-9]{0,4}$";
    private static final String MESSAGE_CONNECTION_ERROR = "Erreur lors de la connexion";

    private static final String MESSAGE_FILE_ERROR = "Erreur lors de l'enregistrement de la configuration";

    private MailConnectionCredentials credentials;

    public void onClickValidate() {
        try {
            setMessageError("", false);
            tryToConnect();
            writeFile();

            showNextScene();
        } catch (Exception e) {
            setMessageError(e.getMessage(), true);
        }
    }

    private void writeFile() throws Exception {
        if (!ConfigFile.writeFile(this.credentials)) {
            throw new Exception(MESSAGE_FILE_ERROR);
        }
    }

    private void tryToConnect() throws Exception {
        try {
            this.credentials = MailConnectionCredentials.builder()
                    .email(this.configEmail.getText())
                    .password(this.configPassword.getText())
                    .smtpHost(this.configAddrSMTP.getText())
                    .smtpPort(Integer.parseInt(this.configPortSMTP.getText()))
                    .imapHost(this.configAddrIMAP.getText())
                    .imapPort(Integer.parseInt(this.configPortIMAP.getText()))
                    .isSSL(this.configSSL.isSelected())
                    .build();
            MailManager mailManager = new MailManager();
            mailManager.setCredentials(credentials);
            mailManager.connect().get();
        } catch (Exception e) {
            throw new Exception(MESSAGE_CONNECTION_ERROR);
        }

    }

    private void showNextScene() throws IOException {
        Stage stage = (Stage) configAnchorPane.getScene().getWindow();
        URL location = getClass().getResource("/fxml/main.fxml");
        Scene scene = new Scene(new FXMLLoader(location).load(), MainApp.SCREEN_WIDTH, MainApp.SCREEN_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    private void setMessageError(String message, boolean isVisible) {
        configMessageError.setText(message);
        configMessageError.setVisible(isVisible);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setTextFormatters();
    }

    private void setTextFormatters() {
        configPortSMTP.setTextFormatter(this.formatInput(PORT_REGEX));
        configPortIMAP.setTextFormatter(this.formatInput(PORT_REGEX));
    }

    private TextFormatter<Boolean> formatInput(String regex) {
        return new TextFormatter<>(change ->
                (change.getControlNewText().matches(regex)) ? change : null);
    }


}
