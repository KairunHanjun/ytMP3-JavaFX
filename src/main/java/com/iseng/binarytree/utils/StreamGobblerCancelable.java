package com.iseng.binarytree.utils;

import java.io.IOException;
import java.io.InputStream;

public class StreamGobblerCancelable extends Thread {

    private InputStream stream;
    private StringBuffer buffer;

    public StreamGobblerCancelable(int iterator, StringBuffer buffer, InputStream stream) {
        this.stream = stream;
        this.buffer = buffer;
        this.setName("StreamGobblerCancelable ("+iterator+")");
        start();
    }

    public void run() {
        try {
            int nextChar;
            while((nextChar = this.stream.read()) != -1 && !isInterrupted()) {
                this.buffer.append((char) nextChar);
            }
            if(interrupted()) return;
        }
        catch (IOException e) {

        }
    }

    public void cancel(){
        interrupt();
     }
    
}