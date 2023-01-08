package com.iseng.binarytree;

import java.io.IOException;
import java.text.ParsePosition;
import java.util.Base64;
import java.util.Objects;
import java.util.function.UnaryOperator;

import org.bouncycastle.jcajce.provider.digest.Keccak;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;

public class SettingCustomDialog extends Dialog<Settings>{
    @FXML
    private TextField Thread, USERNAME;
    @FXML
    private PasswordField PASSWORD;
    @FXML
    private CheckBox BypassLimit, DownloadAllIn, MP4, MP3, OpenSignIn, RememberSetting;
    @FXML
    private ComboBox<String> QualityVideo;
    @FXML
    private ButtonType CancelButton, ApplyButton;
    @FXML
    private Button SignIn, SIGNUP;

    private DialogPane dialogPane =  null;
    private final Keccak.Digest512 hashing = new Keccak.Digest512();
    private Settings settings = Settings.getSettings();

    public SettingCustomDialog(Window onwerWindow, Settings previousSettings){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("option.fxml"));
            fxmlLoader.setController(this);

            dialogPane = fxmlLoader.load();
            dialogPane.lookupButton(ApplyButton).addEventFilter(ActionEvent.ANY, this::onApplying); 

            initOwner(onwerWindow);
            initModality(Modality.APPLICATION_MODAL);
            initStyle(StageStyle.UNDECORATED);

            setResizable(false);
            setTitle("SETTINGS");
            setDialogPane(dialogPane);
            setResultConverter(buttonType -> {
                if(!Objects.equals(ButtonBar.ButtonData.APPLY, buttonType.getButtonData())) return previousSettings;
                return settings;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize(){
        //get json and set all control to that json, if any;
        final NumberStringFilteredConverter converter = new NumberStringFilteredConverter();
        final TextFormatter<Number> formatter = new TextFormatter<>(converter,0,converter.getFilter());
        QualityVideo = setComboBox(QualityVideo);
        Thread.setTextFormatter(formatter);
        if (settings.isRememberSetting()) {
            RememberSetting.setSelected(settings.isRememberSetting());
            Thread.setText(String.valueOf(settings.getThread()));
            BypassLimit.setSelected(settings.isBypassLimit());
            DownloadAllIn.setSelected(settings.isDownloadAllIn());
            OpenSignIn.setSelected(settings.isOpenSignIn());
            USERNAME.setText(settings.getUsername());
            if(settings.isOpenSignIn()){
                USERNAME.setDisable(!settings.isOpenSignIn());
                PASSWORD.setDisable(!settings.isOpenSignIn());
                SignIn.setDisable(!settings.isOpenSignIn());
                SIGNUP.setDisable(!settings.isOpenSignIn());
            }
            if(settings.isMP4()) {
                QualityVideo.setDisable(false);
                QualityVideo.setValue(settings.getQualityVideo());
                MP3.setSelected(false);
                MP4.setSelected(true);
                MP3.setDisable(true);
                MP4.setDisable(false);
            }else if(settings.isMP3()){
                MP3.setSelected(true);
            }
            
        }
    }

    @FXML
    private void LockUnlockSignIn(ActionEvent event){
        boolean OpenSign = OpenSignIn.isSelected();
        if(OpenSign){
            USERNAME.setDisable(!OpenSign);
            PASSWORD.setDisable(!OpenSign);
            SignIn.setDisable(!OpenSign);
            SIGNUP.setDisable(!OpenSign);
        }else{
            USERNAME.setDisable(!OpenSign);
            PASSWORD.setDisable(!OpenSign);
            SignIn.setDisable(!OpenSign);
            SIGNUP.setDisable(!OpenSign);
        }
    }

    //When DownloadAllIn is checked/selected then BypassLimit and Thread would be disabled
    @FXML
    private void AllIn(ActionEvent event){
        if(DownloadAllIn.isSelected()){
            Thread.setText("-1");
            BypassLimit.setDisable(DownloadAllIn.isSelected());
            Thread.setDisable(DownloadAllIn.isSelected());
            return;
        }
        Thread.setText(String.valueOf(settings.getThread()));
        BypassLimit.setDisable(DownloadAllIn.isSelected());
        Thread.setDisable(DownloadAllIn.isSelected());
        return;
    }

    @FXML
    private void LOGIN(ActionEvent event){
        if(PASSWORD.getText().isBlank() || PASSWORD.getText().length() < 8){
            Alert alert = new Alert(AlertType.WARNING, "Password is invalid, please insert anything that have 8 char in it", ButtonType.OK);
            alert.show();
            return;
        }else if(USERNAME.getText().isBlank()){
            Alert alert = new Alert(AlertType.WARNING, "Username is blank, please use it, dont make it waste", ButtonType.OK);
            alert.show();
            return;
        }else if(settings.getPassword().isBlank() || settings.getUsername().isBlank()){
            Alert alert = new Alert(AlertType.WARNING, "The dev didn't make the user by themself, so Sign Up", ButtonType.OK);
            alert.show();
            return;
        }else if(!Keccak.Digest512.isEqual(hashing.digest(PASSWORD.getText().getBytes()), Base64.getDecoder().decode(settings.getPassword()))) {
            Alert alert = new Alert(AlertType.WARNING, "The password is incorrect, If you forget your password you can't sign up again", ButtonType.OK);
            alert.show();
            return;
        }
        settings.setUsername(USERNAME.getText());
        settings.ActualPassword = PASSWORD.getText();
        Alert alert = new Alert(AlertType.INFORMATION, "Login Successful", ButtonType.OK);
        alert.show();
    }

    @FXML
    private void SignUp(ActionEvent event){
        if(PASSWORD.getText().isBlank() || PASSWORD.getText().length() < 8){
            Alert alert = new Alert(AlertType.WARNING, "Password is invalid, please insert anything that have 8 char in it", ButtonType.OK);
            alert.show();
            return;
        }else if(USERNAME.getText().isBlank()){
            Alert alert = new Alert(AlertType.WARNING, "Username is blank, please use it, dont make it waste", ButtonType.OK);
            alert.show();
            return;
        }else if(dialogPane == null){
            Alert alert = new Alert(AlertType.WARNING, "DialogPane is null", ButtonType.OK);
            alert.show();
            return;
        }
        Task<Void> taskHash = new Task<Void>() {
            @Override
            protected Void call(){
                enableButton(false);
                settings.setUsername(USERNAME.getText());
                settings.setPassword(Base64.getEncoder().encodeToString(hashing.digest(PASSWORD.getText().getBytes())));
                settings.ActualPassword = PASSWORD.getText();
                return null;
            }

            @Override
            protected void failed(){
                super.failed();
                Alert alert = new Alert(AlertType.INFORMATION, "SignUp Failed", ButtonType.OK);
                alert.show();
                enableButton(true);
            }

            protected void enableButton(boolean enable){
                Platform.runLater(() -> {
                    USERNAME.setDisable(!enable);
                    PASSWORD.setDisable(!enable);
                    SignIn.setDisable(!enable);
                    SIGNUP.setDisable(!enable);
                    dialogPane.lookupButton(ApplyButton).setDisable(!enable);
                    dialogPane.lookupButton(CancelButton).setDisable(!enable);
                });
            }

            @Override
            protected void succeeded(){
                super.succeeded();
                Alert alert = new Alert(AlertType.INFORMATION, "SignUp Succeeded", ButtonType.OK);
                alert.show();
                enableButton(true);
            }
        };
        new Thread(taskHash).start();
        
    }

    @FXML
    private void TurnOnQualityVideo(ActionEvent event){
        if(!MP4.isSelected()){
            MP4.setDisable(true);
            QualityVideo.setDisable(true);
            MP3.setSelected(true);
            MP3.setDisable(false);
        }
    }

    @FXML
    private void TurnOnMP4(ActionEvent event){
        if(!MP3.isSelected()){
            MP4.setSelected(true);
            MP4.setDisable(false);
            QualityVideo.setDisable(false);
            MP3.setDisable(true);
        }
    }

    @FXML
    private void SelectQuality(ActionEvent event){
        settings.setQualityVideo(QualityVideo.getValue());
    }

    private ComboBox<String> setComboBox(ComboBox<String> comboBox){
        ObservableList<String> listQuality = FXCollections.observableArrayList();
        listQuality.addAll("Lowest Quality", "Medium Quality", "Highest Quality");
        comboBox.setItems(listQuality);
        return comboBox;
    }

    //
    @FXML
    private void onApplying(ActionEvent actionEvent){
        //When Thread is out focusing check if it on limit, unless they bypass it
        if(Integer.compare(Integer.parseInt(Thread.getText()), 20) > 0 && !BypassLimit.isSelected()){
            Alert alert = new Alert(AlertType.WARNING, "Thread is above limit, please use the bypass limit if your cpu can handle it", ButtonType.APPLY);
            alert.show();
            Thread.setText("5");
        }
        settings.setThread(Integer.parseInt(Thread.getText()));
        settings.setBypassLimit(BypassLimit.isSelected());
        settings.setDownloadAllIn(DownloadAllIn.isSelected());
        settings.setMP3(MP3.isSelected());
        settings.setMP4(MP4.isSelected());
        settings.setOpenSignIn(OpenSignIn.isSelected());
        settings.setRememberSetting(RememberSetting.isSelected());
        settings.setQualityVideo(QualityVideo.getValue());
        Settings.setSettings(settings);
    }
    
}

class NumberStringFilteredConverter extends NumberStringConverter {
    // Note, if needed you can add in appropriate constructors 
    // here to set locale, pattern matching or an explicit
    // type of NumberFormat.
    // 
    // For more information on format control, see 
    //    the NumberStringConverter constructors
    //    DecimalFormat class 
    //    NumberFormat static methods for examples.
    // This solution can instead extend other NumberStringConverters if needed
    //    e.g. CurrencyStringConverter or PercentageStringConverter.

    public UnaryOperator<TextFormatter.Change> getFilter() {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }

            ParsePosition parsePosition = new ParsePosition( 0 );
            Object object = getNumberFormat().parse( newText, parsePosition );
            if ( object == null || parsePosition.getIndex() < newText.length()) {
                return null;
            } else {
                return change;
            }
        };
    }
}
