package com.iseng.binarytree;

public class ThreadInterrupt {
    public static Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) 
            if (t.getName().equals(threadName)) 
                return t;
        return null;
    }

    public static void InterruptThread(Thread thread){
        thread.interrupt();
    }
}
