module Sokoban {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;
    requires java.desktop;

    opens org.view.menu to javafx.fxml;
    exports org;
}