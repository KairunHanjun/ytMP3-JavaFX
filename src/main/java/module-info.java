module com.iseng.binarytree {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    
    opens com.iseng.binarytree to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.iseng.binarytree;
    exports com.iseng.binarytree.mapper to com.fasterxml.jackson.databind;
}
