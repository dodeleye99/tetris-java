package com.yahoo.dodeleye.tetris;

public class BlockNotFoundException extends Exception {

    public BlockNotFoundException(String message) {

        super(message);
    }

    public BlockNotFoundException(Throwable throwable){

        super(throwable);
    }

    public BlockNotFoundException(String message, Throwable throwable){

        super(message, throwable);
    }

    public BlockNotFoundException(int x, int y){
        super(("The block to remove from position (" + x + ", " + y + ") is not there."));
    }
}

