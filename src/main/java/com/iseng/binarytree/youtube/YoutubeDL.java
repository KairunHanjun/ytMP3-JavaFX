package com.iseng.binarytree.youtube;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iseng.binarytree.mapper.VideoFormat;
import com.iseng.binarytree.mapper.VideoInfo;
import com.iseng.binarytree.mapper.VideoThumbnail;
import com.iseng.binarytree.utils.StreamGobbler;
import com.iseng.binarytree.utils.StreamProcessExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * <p>Provide an interface for youtube-dl executable</p>
 *
 * <p>
 *     For more information on youtube-dl, please see
 *     <a href="https://github.com/rg3/youtube-dl/blob/master/README.md">YoutubeDL Documentation</a>
 * </p>
 */
public class YoutubeDL {

    /**
     * Youtube-dl executable name
     */
    protected static String executablePath = "youtube-dl";

    /**
     * Patern for regex check if the link is matched link
     */
    protected static final String PATTERN = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
    /**
     * Append executable name to command
     * @param command Command string
     * @return Command string
     */
    protected static String buildCommand(String command) {
        return String.format("%s %s", executablePath, command);
    }

    /**
     * Execute youtube-dl request
     * @param request request object
     * @return response object
     * @throws YoutubeDLException
     */
    public static YoutubeDLResponse execute(YoutubeDLRequest request) throws YoutubeDLException {
        return execute(request, null);
    }

    /**
     * Execute youtube-dl request
     * @param request request object
     * @param callback callback
     * @return response object
     * @throws YoutubeDLException
     */
    public static YoutubeDLResponse execute(YoutubeDLRequest request, DownloadProgressCallback callback) throws YoutubeDLException {

        String command = buildCommand(request.buildOptions());
        String directory = request.getDirectory();
        Map<String, String> options = request.getOption();

        YoutubeDLResponse youtubeDLResponse;
        Process process;
        int exitCode;
        StringBuffer outBuffer = new StringBuffer(); //stdout
        StringBuffer errBuffer = new StringBuffer(); //stderr
        long startTime = System.nanoTime();

        String[] split = command.split(" ");

        ProcessBuilder processBuilder = new ProcessBuilder(split);

        // Define directory if one is passed
        if(directory != null)
            processBuilder.directory(new File(directory));

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new YoutubeDLException(e);
        }

        InputStream outStream = process.getInputStream();
        InputStream errStream = process.getErrorStream();

        StreamProcessExtractor stdOutProcessor = new StreamProcessExtractor(outBuffer, outStream, callback);
        StreamGobbler stdErrProcessor = new StreamGobbler(errBuffer, errStream);

        try {
            stdOutProcessor.join();
            stdErrProcessor.join();
            exitCode = process.waitFor();
        } catch (InterruptedException e) {

            // process exited for some reason
            throw new YoutubeDLException(e);
        }

        String out = outBuffer.toString();
        String err = errBuffer.toString();

        if(exitCode > 0) {
            throw new YoutubeDLException(err);
        }

        int elapsedTime = (int) ((System.nanoTime() - startTime) / 1000000);

        youtubeDLResponse = new YoutubeDLResponse(command, options, directory, exitCode , elapsedTime, out, err);

        return youtubeDLResponse;
    }

    /**
     * Check if the given input by user is valid Youtube Url
     * @param Uri String youtube url
     * @return <p><Strong>true</Strong> if the given url is valid Youtube url</p>
     */
    public static boolean isValidUrl(String Uri){
        if(Uri.isBlank() || !Uri.matches(PATTERN)){
            return false;
        }
        return true;
    }

    /**
     * Get youtube-dl executable version
     * @return version string
     * @throws YoutubeDLException
     */
    public static String getVersion() throws YoutubeDLException {
        YoutubeDLRequest request = new YoutubeDLRequest();
        request.setOption("version");
        return YoutubeDL.execute(request).getOut();
    }

    /**
     * Retrieve all information available on a video
     * @param url Video url
     * @return Video info
     * @throws YoutubeDLException
     */
    public static VideoInfo getVideoInfo(String url) throws YoutubeDLException  {

        // Build request
        YoutubeDLRequest request = new YoutubeDLRequest(url);
        request.setOption("dump-json");
        request.setOption("no-playlist");
        YoutubeDLResponse response = YoutubeDL.execute(request);

        // Parse result
        ObjectMapper objectMapper = new ObjectMapper();
        VideoInfo videoInfo;

        try {
            videoInfo = objectMapper.readValue(response.getOut(), VideoInfo.class);
        } catch (IOException e) {
            throw new YoutubeDLException("Unable to parse video information: " + e.getMessage());
        }

        return videoInfo;
    }
    
    
    /**
     * <p>Retrieve all urls from one playlist,<strong> Use task to call this method for a better experience</strong><p>
     * @param url Playlist url
     * @return List of youtube urls
     * @throws YoutubeDLException
     */
    public static List<String> getPlaylistUrl(String url) throws YoutubeDLException{
        YoutubeDLRequest request = new YoutubeDLRequest(url);
        request.setOption("ignore-errors");
        request.setOption("flat-playlist");
        request.setOption("no-warnings");
        request.setOption("print", "id");
        YoutubeDLResponse response = YoutubeDL.execute(request);

        List<String> urls = new ArrayList<String>();
        for (String id : List.of(response.getOut().split("\\r?\\n"))) {
            urls.add("https://www.youtube.com/watch?v="+id);
        }
        return urls;
    }

    /**
     * <p>Retrieve all ids from one playlist,<strong> Use task to call this method for a better experience</strong><p>
     * @param url Playlist url
     * @return List of youtube ids
     * @throws YoutubeDLException
     */
    public static List<String> getIdList(String url) throws YoutubeDLException{
        YoutubeDLRequest request = new YoutubeDLRequest(url);
        request.setOption("ignore-errors");
        request.setOption("flat-playlist");
        request.setOption("no-warnings");
        request.setOption("print", "id");
        YoutubeDLResponse response = YoutubeDL.execute(request);

        return List.of(response.getOut().split("\\r?\\n"));
    }

    public static List<VideoInfo> getListVideoInfo(String url) throws YoutubeDLException{
        // List<String> Titles = new ArrayList<String>();
        // List<String> ids = YoutubeDL.getIdList(url);
        // for (String title: ids) {
        //     try {
        //         URL json = new URL("https://www.youtube.com/oembed?url=http%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3D"+title+"&format=json");
        //         Titles.add(new JSONObject(IOUtils.toString(json, Charset.defaultCharset())).getString("title"));
        //     } catch (JSONException | IOException e) {}
        // }
        // return Titles;

        YoutubeDLRequest request = new YoutubeDLRequest(url);
        request.setOption("ignore-errors");
        request.setOption("flat-playlist");
        request.setOption("no-warnings");
        request.setOption("dump-json");
        YoutubeDLResponse response = YoutubeDL.execute(request);
        
        List<VideoInfo> listVideoInfo = new ArrayList<VideoInfo>();
        List<String> json = List.of(response.getOut().split("\\r?\\n"));
        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < json.size(); i++) {
            try {
                listVideoInfo.add(objectMapper.readValue(json.get(i), VideoInfo.class));
            } catch (IOException e) {
                throw new YoutubeDLException("Unable to parse video information: " + e.getMessage());
            }
        }

        return listVideoInfo;

    } 

    /**
     * List formats
     * @param url Video url
     * @return list of formats
     * @throws YoutubeDLException
     */
    public static List<VideoFormat> getFormats(String url) throws YoutubeDLException {
        VideoInfo info = getVideoInfo(url);
        return info.formats;
    }

    /**
     * List thumbnails
     * @param url Video url
     * @return list of thumbnail
     * @throws YoutubeDLException
     */
    public static List<VideoThumbnail> getThumbnails(String url) throws YoutubeDLException {
        VideoInfo info = getVideoInfo(url);
        return info.thumbnails;
    }

    /**
     * List categories
     * @param url Video url
     * @return list of category
     * @throws YoutubeDLException
     */
    public static List<String> getCategories(String url) throws YoutubeDLException {
        VideoInfo info = getVideoInfo(url);
        return info.categories;
    }

    /**
     * List tags
     * @param url Video url
     * @return list of tag
     * @throws YoutubeDLException
     */
    public static List<String> getTags(String url) throws YoutubeDLException {
        VideoInfo info = getVideoInfo(url);
        return info.tags;
    }

    /**
     * Get command executable or path to the executable
     * @return path string
     */
    public static String getExecutablePath(){
        return executablePath;
    }

    /**
     * Set path to use for the command
     * @param path String path to the executable
     */
    public static void setExecutablePath(String path){
        executablePath = path;
    }
}
