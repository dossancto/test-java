package org.example.lib;

public class Fetcher {
    private Class<?> clazz = null;
    public Fetcher(Class<?> clazz){
        // Get the application context
        // to use runOnUIThread.
        this.clazz = clazz;
    }

    public Fetcher(){
    }

    public void async(Runnable runnable) throws RuntimeException{
        runnable.run();
    }

    public void asyncUI(Runnable runnable) {
        if(clazz == null){
            throw new IllegalArgumentException("You need to use a context");
        }

        // Run on UI Thread, need context
        runnable.run();
    }
}
