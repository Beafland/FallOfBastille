module com.github.beafland.fallofbastille {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.github.beafland.fallofbastille to javafx.fxml;
    exports com.github.beafland.fallofbastille;
    exports com.github.beafland.fallofbastille.character;
    opens com.github.beafland.fallofbastille.character to javafx.fxml;
}