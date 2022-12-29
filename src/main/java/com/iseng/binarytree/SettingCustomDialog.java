package com.iseng.binarytree;

import java.io.IOException;
import java.text.ParsePosition;
import java.util.Objects;
import java.util.function.UnaryOperator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
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
    private ButtonType CancelButton, ApplyButton;
    @FXML
    private Button SignIn;

    private Settings settings = Settings.getSettings();

    public SettingCustomDialog(Window onwerWindow){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("option.fxml"));
            fxmlLoader.setController(this);

            DialogPane dialogPane = fxmlLoader.load();
            dialogPane.lookupButton(ApplyButton).addEventFilter(ActionEvent.ANY, this::onApplying);

            initOwner(onwerWindow);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(false);
            setTitle("SETTINGS");
            setDialogPane(dialogPane);
            setResultConverter(buttonType -> {
                if(!Objects.equals(ButtonBar.ButtonData.APPLY, buttonType.getButtonData())) return null;
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
        Thread.setTextFormatter(formatter);
        if (settings.isRememberSetting()) {
            RememberSetting.setSelected(settings.isRememberSetting());
            Thread.setText(String.valueOf(settings.getThread()));
            BypassLimit.setSelected(settings.isBypassLimit());
            DownloadAllIn.setSelected(settings.isDownloadAllIn());
            MP3.setSelected(settings.isMP3());
            MP4.setSelected(settings.isMP4());
            OpenSignIn.setSelected(settings.isOpenSignIn());
            USERNAME.setText(settings.getUsername());
        }
    }

    @FXML
    private void LockUnlockSignIn(ActionEvent event){
        boolean OpenSign = OpenSignIn.isSelected();
        if(OpenSign){
            USERNAME.setDisable(!OpenSign);
            PASSWORD.setDisable(!OpenSign);
            SignIn.setDisable(!OpenSign);
        }else{
            USERNAME.setDisable(!OpenSign);
            PASSWORD.setDisable(!OpenSign);
            SignIn.setDisable(!OpenSign);
        }
    }

    @FXML
    private void LOGIN(ActionEvent event){
        settings.setUsername(USERNAME.getText());
    }

    //
    @FXML
    private void onApplying(ActionEvent actionEvent){
        if(OpenSignIn.isSelected() && !USERNAME.getText().isBlank())settings.setUsername(USERNAME.getText());
        settings.setThread(Integer.parseInt(Thread.getText()));
        settings.setBypassLimit(BypassLimit.isSelected());
        settings.setDownloadAllIn(DownloadAllIn.isSelected());
        settings.setMP3(MP3.isSelected());
        settings.setMP4(MP4.isSelected());
        settings.setOpenSignIn(OpenSignIn.isSelected());
        settings.setRememberSetting(RememberSetting.isSelected());
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
