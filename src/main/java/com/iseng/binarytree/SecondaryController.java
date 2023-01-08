package com.iseng.binarytree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.iseng.binarytree.youtube.*;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

public class SecondaryController {
    @FXML
    private VBox LOG;
    @FXML
    private TextField Link, Save;
    @FXML
    private CheckBox isAsyncON, isPlaylist; 
    @FXML
    private AnchorPane disableWhileDownloading;
    @FXML
    private Button DownloadButton;

    //BEGIN DEBUG
    private final String SURL = "https://www.youtube.com/playlist?list=PL28pwkPpXVhRGaYPUP-mhXDLJ86KkxVKM";
    //private final String SDICT = "C:\\FILES\\JAVA\\TEST_DOWNLOAD";
    @FXML
    private void initialize(){
        Link.setText(SURL);
        //Save.setText(SDICT);
    } 
    //END DEBUG

    private Settings settings = Settings.getSettings();
    private int howManyHasBeenDownloaded = 0;
    List<Task<Void>> listWorker = null;
    Task<Void> downloadWrapper = null;

    private YoutubeDLRequest setOptionYoutubeRequest(final String url, final String Where, final boolean isMP4, final boolean isMP3){
        YoutubeDLRequest youtubeRequest = new YoutubeDLRequest(url, Where);
        youtubeRequest.setOption("output", "%(title)s.%(ext)s");
        youtubeRequest.setOption("retries", 10);
        youtubeRequest.setOption("no-warnings");
        if(isMP3){
            youtubeRequest.setOption("extract-audio");
            youtubeRequest.setOption("audio-format", "mp3");
        }else if(isMP4){
            switch (settings.getQualityVideo()) {
                case "Lowest Quality":{
                    youtubeRequest.setOption("format", "\"bestvideo[height<=240][ext=mp4]+bestaudio[ext=m4a]\"");
                    break;
                }
                case "Medium Quality":{
                    youtubeRequest.setOption("format", "\"bestvideo[height<=480][ext=mp4][filesize<70M]+bestaudio[ext=m4a]\"");
                    break;
                }
                case "Highest Quality":{
                    youtubeRequest.setOption("format", "\"bestvideo[height<=1080][ext=mp4]+bestaudio[ext=m4a]\"");
                    break;
                }
                default:
                    break;
            }
        }
        return youtubeRequest;
    }

    private void prepareDownload(String Title, int iterator){
        //New Anchorpanel has been born
        AnchorPane newAnchor = new AnchorPane();
        newAnchor.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        LOG.getChildren().add(newAnchor);

        //New Title has been born
        Label newTitle = new Label(Title);
        newTitle.maxHeight(600);
        newAnchor.getChildren().add(newTitle);

        //Born the triples Node
        ProgressBar newProgressBar = new ProgressBar(0);
        Label newStatus = new Label("Preparing");
        Button newButton = new Button("CANCEL ("+ iterator +")");

        //Child one, name progressbar
        newProgressBar.setPrefWidth(450);
        newProgressBar.setLayoutY(newAnchor.getChildren().get(newAnchor.getChildren().size()-1).getLayoutY()+20);
        newAnchor.getChildren().add(newProgressBar);

        //Child two, hi newStatus
        newStatus.setLayoutY(newAnchor.getChildren().get(newAnchor.getChildren().size()-1).getLayoutY());
        newStatus.setLayoutX(newAnchor.getChildren().get(newAnchor.getChildren().size()-1).getLayoutX()+newProgressBar.getPrefWidth()+5);
        newStatus.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        newAnchor.getChildren().add(newStatus);
        
        //Child three, welcome to world newButton
        newButton.setLayoutY(newStatus.getLayoutY()-7);
        newButton.setLayoutX(newStatus.getLayoutX()+50);
        newButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        newButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                int index = Integer.parseInt(newButton.getText().substring(8, 9));
                listWorker.get(index).cancel(true);
            }
        });
        newAnchor.getChildren().add(newButton);
    }

    //For Playlist url asynchronous download
    private void asyncPlaylistDownload(String Url, String Where) throws YoutubeDLException{
        final Task<YoutubeListInfo> listVideo = new Task<YoutubeListInfo>() {
            //Get value by task
            @Override
            protected YoutubeListInfo call() throws Exception {
                return new YoutubeListInfo(YoutubeDL.getListVideoInfo(Url), YoutubeDL.getPlaylistUrl(Url));
            }
            
            //When succeed get value, start download
            @Override
            protected void succeeded(){
                super.succeeded();
                //Set prepareDownload() equal to size of youtube playlist
                final YoutubeListInfo newListInfo = super.getValue();
                for(int i = 0; i < newListInfo.urls.size(); i++){
                    final int i_ = i;
                    Platform.runLater(() ->prepareDownload(newListInfo.videoInfo.get(i_).title, i_));
                }
                //Here task to download
                downloadWrapper = new Task<Void>() {
                    @Override
                    protected Void call() throws IOException, YoutubeDLException{
                        if(settings.getThread() == -1) {
                            settings.setThread(newListInfo.urls.size());
                        }else if(settings.getThread() > newListInfo.urls.size()){
                            settings.setThread(newListInfo.urls.size());
                        }
                        
                        for(int i_ = 0; i_ < newListInfo.urls.size() && !isCancelled(); i_+=settings.getThread()){ 
                            listWorker = new ArrayList<Task<Void>>(settings.getThread()+1);
                            boolean stateFinish[] = new boolean[settings.getThread()];
                            boolean throwingCrap[] = new boolean[settings.getThread()];           
                            for(int j = 0; j < settings.getThread(); j++){
                                final int j_=j;
                                final int i__ = i_;
                                howManyHasBeenDownloaded++;
                                final int i = howManyHasBeenDownloaded-1;
                                Task<Void> toDownload = new Task<Void>() {
                                    @Override
                                    protected Void call() throws RuntimeException, IOException {
                                        //Perform download youtube video
                                        
                                        Platform.runLater(() -> {
                                            try{
                                                ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Running");}
                                            catch(IndexOutOfBoundsException e){
                                                throwingCrap[j_] = true;}});
                                        if(throwingCrap[j_] || isCancelled()) 
                                            return null;
                                        try {
                                            YoutubeDL.execute(setOptionYoutubeRequest(newListInfo.urls.get(i__+j_), Where, settings.isMP4(), settings.isMP3()), i, true, new DownloadProgressCallback() {
                                                @Override
                                                public void onProgressUpdate(float progress, long etaInSeconds) {
                                                    Platform.runLater(() -> {
                                                        if(downloadWrapper.isCancelled())listWorker.get(i).cancel();
                                                        ((ProgressBar)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(1)).setProgress((progress/100));
                                                        ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText(String.valueOf(progress)+"%");
                                                        if(progress == 100.0)
                                                            ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Finishing");
                                                    });
                                                }});
                                        } catch (YoutubeDLException e2) {       
                                            Platform.runLater(() -> {
                                                if(!isCancelled()){
                                                    ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Failed");
                                                }else{
                                                    ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Cancelled");
                                                    stateFinish[j_] = true;
                                                    ThreadInterrupt.InterruptThread(ThreadInterrupt.getThreadByName("StreamProcessExtractorCancelable ("+i+")"));
                                                    ThreadInterrupt.InterruptThread(ThreadInterrupt.getThreadByName("StreamGobblerCancelable ("+i+")"));
                                                    //TODO: Please implement delete trash if the download is cancelled;
                                                }
                                                ((ProgressBar)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(1)).setProgress(1);
                                                ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(0)).setText(((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(0)).getText()+" "+e2.getMessage().substring(e2.getMessage().lastIndexOf(":"), e2.getMessage().lastIndexOf(".")));
                                                throwingCrap[j_] = true;
                                            });
                                        }
                                        return null;
                                    }

                                    @Override protected void succeeded(){
                                        super.succeeded();
                                        if(throwingCrap[j_]) {stateFinish[j_] = true; return;}
                                        Platform.runLater(() -> {
                                            ((ProgressBar)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(1)).setProgress(1);
                                            ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Finish");
                                        });
                                        stateFinish[j_] = true;
                                    }
                                };
                                listWorker.add(toDownload);
                            }
                            for (int i = 0; i < listWorker.size(); i++) {
                                Thread t = new Thread(listWorker.get(i));
                                t.setName("Worker ("+ i +")");
                                t.start();
                            }
                            for(int i = 0; i < settings.getThread(); i++) while(!stateFinish[i]){if(isCancelled())break;};
                            
                        }
                        Platform.runLater(() -> DownloadButton.setDisable(false));
                        return null;
                    }};
                Thread thread = new Thread(downloadWrapper);
                thread.setName("downloadWrapper");
                thread.start();
            }
        };
        Thread threads = new Thread(listVideo);
        threads.setName("listVideo");
        threads.start(); 
    }

    //For Single Url Asynchronous Download
    private void asyncDownload(String Url, String Where){
        
        downloadWrapper = new Task<Void>() {
            @Override
            protected Void call() {
                howManyHasBeenDownloaded++;
                final int i = howManyHasBeenDownloaded-1;

                Platform.runLater(() -> {
                    try {prepareDownload(YoutubeDL.getVideoInfo(Url).title, i);}
                    catch (YoutubeDLException e) {}
                    ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Running");
                });
                //Perform download youtube video 
                try {
                    YoutubeDL.execute(setOptionYoutubeRequest(Url, Where, settings.isMP4(), settings.isMP3()), new DownloadProgressCallback() {
                        @Override
                        public void onProgressUpdate(float progress, long etaInSeconds) {
                            Platform.runLater(() -> {
                                ((ProgressBar)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(1)).setProgress((progress/100));
                                if(progress == 100.0){
                                    Platform.runLater(() -> ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Finishing"));
                                }
                            });
                        }
                    });
                } catch (YoutubeDLException e2) {
                    Platform.runLater(() -> {
                        ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Failed");
                        ((ProgressBar)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(1)).setProgress(1);
                        ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(0)).setText(((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(0)).getText()+" "+e2.getMessage().substring(e2.getMessage().lastIndexOf(":"), e2.getMessage().lastIndexOf(".")));
                    });
                }
                Platform.runLater(() -> {
                    ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Finish");
                    disableWhileDownloading.setDisable(isCancelled());
                    DownloadButton.setDisable(false);
                });
                return null;
            }};
        Thread e = new Thread(downloadWrapper);
        e.setName("youtubeDownload");
        e.start();
    }

    //For Playlist Sychronous Download
    private void syncPlaylistDownload(String Url, String Where) throws YoutubeDLException{
        Task<Void> workHarder = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                YoutubeListInfo finalshit = new YoutubeListInfo(YoutubeDL.getListVideoInfo(Url), YoutubeDL.getPlaylistUrl(Url));
                Platform.runLater(() -> disableWhileDownloading.setDisable(true));
                Task<Boolean> downloadYoutube = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        for (int i = 0; i < finalshit.urls.size(); i++) {                            
                            //Perform download youtube video
                            howManyHasBeenDownloaded++;
                            final int i_ = howManyHasBeenDownloaded-1;
                            Platform.runLater(() -> {
                                prepareDownload(finalshit.videoInfo.get(i_).title, i_);
                                ((Label)((AnchorPane)LOG.getChildren().get(i_)).getChildren().get(2)).setText("Running");});
                            try{
                                YoutubeDL.execute(setOptionYoutubeRequest(finalshit.urls.get(i_), Where, settings.isMP4(), settings.isMP3()), new DownloadProgressCallback() {
                                    @Override
                                    public void onProgressUpdate(float progress, long etaInSeconds) {
                                        Platform.runLater(() -> {
                                            ((Label)((AnchorPane)LOG.getChildren().get(i_)).getChildren().get(2)).setText(String.valueOf(progress)+"%");
                                            ((ProgressBar)((AnchorPane)LOG.getChildren().get(i_)).getChildren().get(1)).setProgress((progress/100));
                                            if(progress == 100.0) ((Label)((AnchorPane)LOG.getChildren().get(i_)).getChildren().get(2)).setText("Finishing");
                                        });
                                    }
                                });
                                Platform.runLater(() -> {
                                    ((ProgressBar)((AnchorPane)LOG.getChildren().get(i_)).getChildren().get(1)).setProgress(1);
                                    ((Label)((AnchorPane)LOG.getChildren().get(i_)).getChildren().get(2)).setText("Finish");
                                });
                            }catch(YoutubeDLException e2){
                                Platform.runLater(() -> {
                                    ((Label)((AnchorPane)LOG.getChildren().get(i_)).getChildren().get(2)).setText("Failed");
                                    ((ProgressBar)((AnchorPane)LOG.getChildren().get(i_)).getChildren().get(1)).setProgress(1);
                                    ((Label)((AnchorPane)LOG.getChildren().get(i_)).getChildren().get(0)).setText(((Label)((AnchorPane)LOG.getChildren().get(i_)).getChildren().get(0)).getText()+" "+e2.getMessage().substring(e2.getMessage().lastIndexOf(":"), e2.getMessage().lastIndexOf(".")));
                                });
                            }
                        }
                        Platform.runLater(() -> {
                            disableWhileDownloading.setDisable(false);
                            DownloadButton.setDisable(false);
                        });
                        return true;
                    }
                };

                Thread e = new Thread(downloadYoutube);
                e.setName("youtubeDownload");
                e.start();      
                return null;
            }
        };
        new Thread(workHarder).start(); 
    }

    //For Single Url Synchorous Download
    private void syncDownload(String Url, String Where){
        disableWhileDownloading.setDisable(true);
        
        Task<Boolean> downloadYoutube = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                howManyHasBeenDownloaded++;
                final int i = howManyHasBeenDownloaded-1;
                //Perform download youtube video        
                Platform.runLater(() -> {
                    try {prepareDownload(YoutubeDL.getVideoInfo(Url).title, i);}
                    catch (YoutubeDLException e1) {prepareDownload("ERROR: Cannot get title youtube", i);}
                    ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Running");});
                try{
                    YoutubeDL.execute(setOptionYoutubeRequest(Url, Where, settings.isMP4(), settings.isMP3()), new DownloadProgressCallback() {
                        @Override
                        public void onProgressUpdate(float progress, long etaInSeconds) {
                            Platform.runLater(() -> {
                                ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText(String.valueOf(progress)+"%");
                                ((ProgressBar)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(1)).setProgress((progress/100));
                                if(progress == 100.0) Platform.runLater(() -> ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Finishing"));
                            });
                        }
                    });
                    Platform.runLater(() -> {
                        ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Finish");
                        disableWhileDownloading.setDisable(false);
                        DownloadButton.setDisable(false);
                    });
                }catch(YoutubeDLException e2){
                    Platform.runLater(() -> {
                        ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Failed");
                        ((ProgressBar)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(1)).setProgress(1);
                        ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(0)).setText(((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(0)).getText()+" "+e2.getMessage().substring(e2.getMessage().lastIndexOf(":"), e2.getMessage().lastIndexOf(".")));
                        disableWhileDownloading.setDisable(false);
                        DownloadButton.setDisable(false);
                    });
                }
                return true;
            }
        };

        Thread e = new Thread(downloadYoutube);
        e.setName("youtubeDownload");
        e.start();
    }

    @FXML
    void showSetting(MouseEvent eventHandler) throws IOException{
        SettingCustomDialog settingCustomDialog = new SettingCustomDialog(Window.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null), settings);
        settingCustomDialog.showAndWait().ifPresent(result -> settings = result);
        settings.isMP3();
    }

    @FXML
    void downloadYoutube(ActionEvent event) throws IOException {
        DownloadButton.setDisable(true);
        //Creating Threading for youtube download
        if(Link.getText().isBlank()){
            Alert missingLink = new Alert(AlertType.WARNING, "Link is missing, please provide the link", ButtonType.OK);
            missingLink.show();
            DownloadButton.setDisable(false);
            return;
        }else if(Save.getText().isBlank()){
            Alert missingLink = new Alert(AlertType.WARNING, "Dictory is missing, where you gonna put the file?", ButtonType.OK);
            missingLink.show();
            DownloadButton.setDisable(false);
            return;
        }
        //Begin Download
        if(isAsyncON.isSelected()){
            if(isPlaylist.isSelected()){
                try {
                    asyncPlaylistDownload(Link.getText(), Save.getText());
                } catch (YoutubeDLException e) {e.printStackTrace();}
            }else{
                if(Link.getText().contains("playlist")){
                    Alert DickHead = new Alert(AlertType.ERROR, "Please, check the isPlaylist CheckBox if this link was a playlist", ButtonType.OK);
                    DickHead.show();
                    DownloadButton.setDisable(false);
                    return;
                }
                asyncDownload(Link.getText(), Save.getText());
            } 
        }else{
            if(YoutubeDL.isValidUrl(Link.getText())){
                if(isPlaylist.isSelected() && Link.getText().contains("playlist")){
                    try {syncPlaylistDownload(Link.getText(), Save.getText());}
                    catch (YoutubeDLException e) {}
                }else{
                    if(Link.getText().contains("playlist")){
                        Alert DickHead = new Alert(AlertType.ERROR, "Please, check the isPlaylist CheckBox if this link was a playlist", ButtonType.OK);
                        DickHead.show();
                        DownloadButton.setDisable(false);
                        return;
                    }
                    syncDownload(Link.getText(), Save.getText());
                }
            }
        }
    }

    @FXML
    void whereToSaveTo(ActionEvent event) throws IOException, YoutubeDLException{
        DirectoryChooser where = new DirectoryChooser();
        where.setInitialDirectory(new File("C:\\"));
        Save.setText(where.showDialog(Window.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null)).getAbsolutePath());
    }

    @FXML
    void clearingVbox(ActionEvent event) throws IOException{
        if(downloadWrapper != null || downloadWrapper.isRunning()){
            downloadWrapper.cancel(true);
        }
        LOG.getChildren().clear();
        howManyHasBeenDownloaded = 0;
    }

    @FXML
    void switchToPrimary(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }
}
