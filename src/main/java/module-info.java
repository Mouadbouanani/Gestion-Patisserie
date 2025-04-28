module com.gestionpatisserie {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.pastrymanagement to javafx.fxml;
    exports com.pastrymanagement;
}