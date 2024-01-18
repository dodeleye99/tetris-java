package com.yahoo.dodeleye.tetris;

public class BlockAlreadyAtPositonException extends Exception {

    public BlockAlreadyAtPositonException(String message) {

        super(message);
    }

    public BlockAlreadyAtPositonException(Throwable throwable){

        super(throwable);
    }

    public BlockAlreadyAtPositonException(String message, Throwable throwable){

        super(message, throwable);
    }

    public BlockAlreadyAtPositonException(int x, int y){
        super(("A block is already at the position to add a block to: (" + x + ", " + y + ")"));
    }
}
