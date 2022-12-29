package com.iseng.binarytree;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Settings{
    private boolean BypassLimit;
    private boolean DownloadAllIn;
    private boolean MP4;
    private boolean MP3;
    private boolean OpenSignIn;
    private boolean RememberSetting;
    private int Thread;
    private String Username;

    public Settings(boolean bypassLimit, boolean downloadAllIn, boolean mP4, boolean mP3, boolean openSignIn,
            boolean rememberSetting, int thread, String username) {
        BypassLimit = bypassLimit;
        DownloadAllIn = downloadAllIn;
        MP4 = mP4;
        MP3 = mP3;
        OpenSignIn = openSignIn;
        RememberSetting = rememberSetting;
        Thread = thread;
        Username = username;
    }

    public Settings(){}

    public void resetProperties(){
        this.BypassLimit = false;
        this.DownloadAllIn = false;
        this.MP3 = false;
        this.MP4 = false;
        this.OpenSignIn = false;
        this.RememberSetting = false;
        this.Thread = 5;
        this.Username = "";
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
        return true;
    }

    @Override
    public String toString() {
        return "Settings [BypassLimit=" + BypassLimit + ", DownloadAllIn=" + DownloadAllIn + ", MP4=" + MP4 + ", MP3="
                + MP3 + ", OpenSignIn=" + OpenSignIn + ", RememberSetting=" + RememberSetting + ", Thread=" + Thread
                + ", Username=" + Username + "]";
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public static Settings getSettings(){
        try{
            File streamSettings = new File("setting.json");
            Settings setting = new Settings();
            if(streamSettings.createNewFile()){
                setting = new Settings();
                setting.resetProperties();
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
}