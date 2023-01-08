package com.iseng.binarytree.youtube;
public interface DownloadProgressCallback {
    void onProgressUpdate(float progress, long etaInSeconds);   
}