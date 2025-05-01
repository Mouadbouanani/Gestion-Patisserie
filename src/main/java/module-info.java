module com.gestionpatisserie {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    exports com.pastrymanagement.controller;
    opens com.pastrymanagement.controller to javafx.fxml;
    exports com.pastrymanagement.model;
    opens com.pastrymanagement.model to javafx.fxml;
    exports com.pastrymanagement;
    opens com.pastrymanagement to javafx.fxml;
    exports com.pastrymanagement.service;
    opens com.pastrymanagement.service to javafx.fxml;
    exports com.pastrymanagement.util;
    opens com.pastrymanagement.util to javafx.fxml;
}