package com.iseng.binarytree;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Settings{
    public static Settings setDefaultProperties(){
        Settings settings = new Settings();
        settings.resetSetting();
        return settings;
    }
    public static Settings getSettings(){
        try{
            File streamSettings = new File("setting.json");
            Settings setting = Settings.setDefaultProperties();
            if(streamSettings.createNewFile()){
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(streamSettings, setting);
            }else{
                setting = (new ObjectMapper()).readValue(streamSettings, Settings.class);
            }
            return setting;
        }catch(IOException e){return null;}
    }
    public static void setSettings(Settings settings){
        try{
            (new ObjectMapper()).writeValue(new File("setting.json"), settings);
        }catch(IOException e){
            throw new RuntimeException("Error: Can't set the settings");
        }
    }
    private boolean BypassLimit;
    private boolean DownloadAllIn;
    private boolean MP4;
    private boolean MP3;
    private boolean OpenSignIn;
    private boolean RememberSetting;

    private int Thread;

    private String Username;

    private String Password;

    private String QualityVideo;

    @JsonIgnore
    public String ActualPassword;
    public Settings(){
        this.resetSetting();
    }
    public String getQualityVideo() {
        return QualityVideo;
    }

    public void setQualityVideo(String qualityVideo) {
        QualityVideo = qualityVideo;
    }

    public void resetSetting(){
        this.BypassLimit = false;
        this.DownloadAllIn = false;
        this.MP3 = false;
        this.MP4 = true;
        this.OpenSignIn = false;
        this.RememberSetting = false;
        this.Thread = 5;
        this.Username = "";
        this.Password = "";
        this.ActualPassword = "";
        this.QualityVideo = "Lowest Quality";
    }

    public boolean isBypassLimit() {
        return BypassLimit;
    }

    public void setBypassLimit(boolean bypassLimit) {
        BypassLimit = bypassLimit;
    }

    public boolean isDownloadAllIn() {
        return DownloadAllIn;
    }

    public void setDownloadAllIn(boolean downloadAllIn) {
        DownloadAllIn = downloadAllIn;
    }

    public boolean isMP4() {
        return MP4;
    }

    public void setMP4(boolean mP4) {
        MP4 = mP4;
    }

    public boolean isMP3() {
        return MP3;
    }

    public void setMP3(boolean mP3) {
        MP3 = mP3;
    }

    public boolean isOpenSignIn() {
        return OpenSignIn;
    }

    public void setOpenSignIn(boolean openSignIn) {
        OpenSignIn = openSignIn;
    }

    public boolean isRememberSetting() {
        return RememberSetting;
    }

    public void setRememberSetting(boolean rememberSetting) {
        RememberSetting = rememberSetting;
    }

    public int getThread() {
        return Thread;
    }

    public void setThread(int thread) {
        Thread = thread;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

   

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (BypassLimit ? 1231 : 1237);
        result = prime * result + (DownloadAllIn ? 1231 : 1237);
        result = prime * result + (MP4 ? 1231 : 1237);
        result = prime * result + (MP3 ? 1231 : 1237);
        result = prime * result + (OpenSignIn ? 1231 : 1237);
        result = prime * result + (RememberSetting ? 1231 : 1237);
        result = prime * result + Thread;
        result = prime * result + ((Username == null) ? 0 : Username.hashCode());
        result = prime * result + ((Password == null) ? 0 : Password.hashCode());
        result = prime * result + ((QualityVideo == null) ? 0 : QualityVideo.hashCode());
        result = prime * result + ((ActualPassword == null) ? 0 : ActualPassword.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Settings other = (Settings) obj;
        if (BypassLimit != other.BypassLimit)
            return false;
        if (DownloadAllIn != other.DownloadAllIn)
            return false;
        if (MP4 != other.MP4)
            return false;
        if (MP3 != other.MP3)
            return false;
        if (OpenSignIn != other.OpenSignIn)
            return false;
        if (RememberSetting != other.RememberSetting)
            return false;
        if (Thread != other.Thread)
            return false;
        if (Username == null) {
            if (other.Username != null)
                return false;
        } else if (!Username.equals(other.Username))
            return false;
        if (Password == null) {
            if (other.Password != null)
                return false;
        } else if (!Password.equals(other.Password))
            return false;
        if (QualityVideo == null) {
            if (other.QualityVideo != null)
                return false;
        } else if (!QualityVideo.equals(other.QualityVideo))
            return false;
        if (ActualPassword == null) {
            if (other.ActualPassword != null)
                return false;
        } else if (!ActualPassword.equals(other.ActualPassword))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "Settings [BypassLimit=" + BypassLimit + ", DownloadAllIn=" + DownloadAllIn + ", MP4=" + MP4 + ", MP3="
                + MP3 + ", OpenSignIn=" + OpenSignIn + ", RememberSetting=" + RememberSetting + ", Thread=" + Thread
                + ", Username=" + Username + "]";
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}