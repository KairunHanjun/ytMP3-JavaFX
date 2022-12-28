package com.iseng.binarytree;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class PrimaryController {
    @FXML
    private TextField BinaryTreeInput;
    @FXML
    private ScrollPane ShowTree;
    @FXML
    private VBox ShowData;

    private Properties datas = new Properties(200);

    @FXML
    private void addBinaryTree(ActionEvent action) throws IOException{
        datas.setValue(BinaryTreeInput.getText());
        BinaryTreeInput.clear();
    }

    private void setShowVisible(){{
        ShowTree.setDisable(false);
        ShowTree.setVisible(true);
        ShowData.setDisable(false);
        ShowData.setVisible(true);
    }}

    @FXML
    private void searchBinaryTree(ActionEvent action) throws IOException{
        ShowData.getChildren().clear();
        if(ShowData.isDisable() && ShowTree.isDisable()){
            setShowVisible();
        }
        String data = datas.findValue(BinaryTreeInput.getText());
        if(data != "NULL") ShowData.getChildren().add(new Label(data));
        BinaryTreeInput.clear();
    }

    @FXML
    private void onEnterPressed(KeyEvent key) throws IOException{
        if(key.getCode().equals(KeyCode.ENTER)){
            datas.setValue(BinaryTreeInput.getText());
            BinaryTreeInput.setText("");
        }
        
    }

    @FXML
    private void showTree(ActionEvent action) throws IOException{
        ShowData.getChildren().clear();
        if(ShowData.isDisable() && ShowTree.isDisable()){
            setShowVisible();
        }
        for (String data : datas.getData()) {
            if(data != null) ShowData.getChildren().add(new Label(data));    
        }
        
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("testSecondary");
    }
}
