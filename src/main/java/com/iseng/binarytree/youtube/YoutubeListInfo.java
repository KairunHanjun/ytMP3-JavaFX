package com.iseng.binarytree.youtube;

import java.util.ArrayList;
import java.util.List;

import com.iseng.binarytree.mapper.VideoInfo;

public class YoutubeListInfo {
    public List<VideoInfo> videoInfo = new ArrayList<VideoInfo>();
    public List<String> urls = new ArrayList<String>();

    public YoutubeListInfo(List<VideoInfo> videoInfo, List<String> urls){
        this.videoInfo = videoInfo;
        this.urls = urls;
    }
}
