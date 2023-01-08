module com.iseng.binarytree {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.bouncycastle.pkix;
    requires org.bouncycastle.provider;


    
    opens com.iseng.binarytree to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.iseng.binarytree;
    exports com.iseng.binarytree.mapper to com.fasterxml.jackson.databind;
}
