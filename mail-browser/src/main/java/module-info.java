module mail.browser.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.mail;
    requires lombok;

    opens fr.leswebdevs to javafx.fxml;
    opens fr.leswebdevs.controller to javafx.fxml;
    exports fr.leswebdevs;
}