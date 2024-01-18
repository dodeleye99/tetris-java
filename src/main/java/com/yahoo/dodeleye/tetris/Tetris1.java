package com.yahoo.dodeleye.tetris;

import javax.swing.*;
import java.awt.*;

import com.yahoo.dodeleye.GameAudio;
import com.yahoo.dodeleye.tetris.controller.MyGameThread;
import com.yahoo.dodeleye.tetris.model.GameLogic;
import com.yahoo.dodeleye.tetris.view.GamePanel;

//getClass().getResource("somefile").getFile()

/**
 * Represents the top-level window of the program.
 *
 * @author daniel
 * @author dodeleye@yahoo.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class Tetris1 extends JFrame {

    /**
     * The fixed width of the window.
     */
    public static final int WIDTH = 400;
    /**
     * The fixed height of the window.
     */
    public static final int HEIGHT = 500;

    /**
     * The singleton instance of the class that can be accessed.
     */
    private static Tetris1 mainFrame;

    /**
     * The panel to create a visual image of the game.
     */
    private GamePanel gamePanel;

    /**
     * The model of the game to deal with all game logic.
     */
    private static GameLogic model;

    public static final Color BG_COLOUR = Color.BLACK;

    public static void main(String[] args) {

        // Create new model instance
        model = new GameLogic();
        // Create new frame window.
        mainFrame = new Tetris1();

        // Create a new thread and immediately start it.
        new Thread(new MyGameThread()).start();
    }

    /**
     * Gets the frame window.
     *
     * @return the single frame used for the program.
     */
    public static Tetris1 getMainFrame() {

        return mainFrame;
    }

    /**
     * Gets the game panel.
     *
     * @return the single panel for representing the view of the game.
     */
    public static GamePanel getGamePanel() {

        return mainFrame.gamePanel;
    }

    /**
     * Gets the game model
     *
     * @return the model component used for the game logic.
     */
    public static GameLogic getGameModel() {

        return model;
    }

    /**
     * Initialise the frame.
     */
    private Tetris1() {

        // Set the title of the frame.
        setTitle("Tetris1");

        // Change setting such that when the program ends when the exit button is pressed.
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set the size of the window.
        setSize(WIDTH, HEIGHT);

        // Set the position of the window to the centre of the screen.
        setLocationRelativeTo(null);

        // Create an instance of the game panel.
        gamePanel = new GamePanel();
        // Initialise the game panel (necessary?)
        gamePanel.init();
        // Add the panel to the frame.
        add(gamePanel);

        // Lock the size of the frame.
        setResizable(false);

        // Update the visible state of the frame.
        setVisible(true);
    }

}

