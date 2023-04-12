module mail.browser.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.mail;

    opens fr.leswebdevs to javafx.fxml;
    exports fr.leswebdevs;
}