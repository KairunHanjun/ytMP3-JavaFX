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
    private int howManyHasBeenDownloaded = 0;
    private YoutubeDLRequest youtubeRequest;
    
    private void prepareDownload(String Title){
        //New Anchorpanel has been born
        AnchorPane newAnchor = new AnchorPane();
        newAnchor.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        LOG.getChildren().add(newAnchor);

        //New Title has been born
        Label newTitle = new Label(Title);
        newAnchor.getChildren().add(newTitle);

        //Born the triples Node
        ProgressBar newProgressBar = new ProgressBar(0);
        Label newStatus = new Label("Preparing");
        Button newButton = new Button("CANCEL");

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
                //TODO: Cancel Download Code Contruction
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
                    Platform.runLater(() ->prepareDownload(newListInfo.videoInfo.get(i_).title));
                }

                //Here task to download
                Task<Void> downloadWrapper = new Task<Void>() {
                    @Override
                    protected Void call() throws IOException, YoutubeDLException{
                        try{
                            for(int i_ = 0; i_ < newListInfo.urls.size(); i_+=5){ 
                                List<Task<Void>> listWorker = new ArrayList<Task<Void>>();
                                boolean stateFinish[] = new boolean[5];
                                boolean throwingCrap[] = new boolean[5];           
                                for(int j = 0; j < 5; j++){
                                    final int j_=j;
                                    final int i__ = i_;
                                    howManyHasBeenDownloaded++;
                                    final int i = howManyHasBeenDownloaded-1;
                                    System.out.println("Itteration: i: "+i_+" j: "+j);
                                    System.out.println("URLS: "+newListInfo.urls.get(i_+j));
                                    System.out.println("TITLE: "+newListInfo.videoInfo.get(i_+j).title);
                                    Task<Void> toDownload = new Task<Void>() {
                                        @Override
                                        protected Void call() throws RuntimeException, IOException {
                                            //Perform download youtube video
                                            Platform.runLater(() -> ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Running"));
                                            YoutubeDLRequest youtubeRequest = new YoutubeDLRequest(newListInfo.urls.get(i__+j_), Where);
                                            youtubeRequest.setOption("output", "%(title)s.%(ext)s");
                                            youtubeRequest.setOption("retries", 10);
                                            youtubeRequest.setOption("extract-audio");
                                            youtubeRequest.setOption("audio-format", "mp3");
                                            youtubeRequest.setOption("no-warnings");  
                                            try {
                                            YoutubeDLResponse Debug = YoutubeDL.execute(youtubeRequest, new DownloadProgressCallback() {
                                                    @Override
                                                    public void onProgressUpdate(float progress, long etaInSeconds) {
                                                        Platform.runLater(() -> {
                                                            ((ProgressBar)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(1)).setProgress((progress/100));
                                                            if(progress == 100.0)
                                                                Platform.runLater(() -> ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Finishing"));
                                                        });
                                                    }});
                                            System.out.println(Debug.getOut());
                                            } catch (YoutubeDLException e2) {       
                                                Platform.runLater(() -> {
                                                    ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Failed");
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

                                        @Override protected void failed(){
                                            super.failed();
                                            stateFinish[j_] = true;
                                        }
                                    };
                                    listWorker.add(toDownload);
                                }
                                for (Task<Void> worker : listWorker) new Thread(worker).start();
                                for (int e = 0; e < 5; e++) {
                                    while(!stateFinish[e]){}
                                }
                            }
                        }catch (IndexOutOfBoundsException eq){}
                        return null;
                    }};
                new Thread(downloadWrapper).start();
            }
        };
        new Thread(listVideo).start(); 
    }

    //For Single Url Asynchronous Download
    private void asyncDownload(String Url, String Where){
        try {prepareDownload(YoutubeDL.getVideoInfo(Url).title);}
        catch (YoutubeDLException e) {}
        Task<Boolean> downloadYoutube = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                howManyHasBeenDownloaded++;
                final int i = howManyHasBeenDownloaded-1;
                Platform.runLater(() -> ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Running"));
                //Perform download youtube video
                youtubeRequest = new YoutubeDLRequest(Url, Where);
                youtubeRequest.setOption("output", "%(title)s.%(ext)s");
                youtubeRequest.setOption("retries", 10);
                youtubeRequest.setOption("extract-audio");
                youtubeRequest.setOption("audio-format", "mp3");
                youtubeRequest.setOption("ignore-errors");
                youtubeRequest.setOption("no-warnings");  
                try {
                    YoutubeDL.execute(youtubeRequest, new DownloadProgressCallback() {
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
                });
                return true;
            }};
        Thread e = new Thread(downloadYoutube);
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
                            youtubeRequest = new YoutubeDLRequest(finalshit.urls.get(i_), Where);
                            Platform.runLater(() ->prepareDownload(finalshit.videoInfo.get(i_).title));
                            Platform.runLater(() -> ((Label)((AnchorPane)LOG.getChildren().get(i_)).getChildren().get(2)).setText("Running"));
                            youtubeRequest.setOption("output", "%(title)s.%(ext)s");
                            youtubeRequest.setOption("retries", 10);
                            youtubeRequest.setOption("extract-audio");
                            youtubeRequest.setOption("audio-format", "mp3");  
                            youtubeRequest.setOption("no-warnings");
                            try{
                                YoutubeDL.execute(youtubeRequest, new DownloadProgressCallback() {
                                    @Override
                                    public void onProgressUpdate(float progress, long etaInSeconds) {
                                        Platform.runLater(() -> {
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
        try {prepareDownload(YoutubeDL.getVideoInfo(Url).title);}
        catch (YoutubeDLException e1) {prepareDownload("ERROR: Cannot get title youtube");}
        Task<Boolean> downloadYoutube = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                howManyHasBeenDownloaded++;
                final int i = howManyHasBeenDownloaded-1;
                //Perform download youtube video        
                Platform.runLater(() -> ((Label)((AnchorPane)LOG.getChildren().get(i)).getChildren().get(2)).setText("Running"));
                youtubeRequest = new YoutubeDLRequest(Url, Where);
                youtubeRequest.setOption("output", "%(title)s.%(ext)s");
                youtubeRequest.setOption("retries", 10);
                youtubeRequest.setOption("extract-audio");
                youtubeRequest.setOption("audio-format", "mp3");
                youtubeRequest.setOption("ignore-errors");
                youtubeRequest.setOption("no-warnings");
                try{
                    YoutubeDL.execute(youtubeRequest, new DownloadProgressCallback() {
                        @Override
                        public void onProgressUpdate(float progress, long etaInSeconds) {
                            Platform.runLater(() -> {
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
    void downloadYoutube(ActionEvent event) throws IOException {
        DownloadButton.setDisable(true);
        //Creating Threading for youtube download
        if(Link.getText().isBlank()){
            Alert missingLink = new Alert(AlertType.WARNING, "Link is missing, please provide the link", ButtonType.OK);
            missingLink.show();
            DownloadButton.setDisable(false);
            return;
        }else if(Save.getText().isBlank()){
            Alert missingLink = new Alert(AlertType.WARNING, "Dictory is missing, please provide the link", ButtonType.OK);
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
        LOG.getChildren().clear();
        howManyHasBeenDownloaded = 0;
    }

    @FXML
    void switchToPrimary(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }
}
