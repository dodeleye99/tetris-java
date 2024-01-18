package com.yahoo.dodeleye.tetris.model;

import com.yahoo.dodeleye.GameAudio;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Represents the model component of the game, covering all the game logic.
 *
 * @version 1.0.1
 * @since 1.0.0
 */
public class GameLogic {

    /**
     * The current score the player has reached in the game.
     */
    private int currentScore = 0;
    /**
     * The all-time best score obtained by a player
     */
    private int highScore;
    /**
     * The number of full rows of blocks cleared from the grid.
     */
    private int lines = 0;
    /**
     * The current level of the game.
     * <p>
     * The higher the level, the faster the tetromino piece will fall.
     * </p>
     */
    private int level = 1;
    /**
     * The lines required to reach the next level.
     */
    private int linesToNextLevel = 10;
    /**
     * A counter to determine how many lines left to reach next level.
     */
    private int linesLeft;

    /**
     * Is a Tetris game being played at the moment?
     */
    private boolean gamePlaying;

    /**
     * The next tetromino piece, waiting to spawn after the current one.
     */
    private Tetromino nextPiece;

    /**
     * The playfield where the game takes place on.
     */
    private Playfield grid = new Playfield(this);

    /**
     * Holds the current list of types for the tetromino pieces waiting to be generated.
     */
    private List<Tetromino.Type> bag;

    /**
     * The file storing the scores obtained from the game.
     */
    private ScoresFile scoresFile;

    public GameLogic() {

        // Start by filling up the tetromino bag.
        refillBag();

        gamePlaying = true;

        // Initialise the scores file.
        scoresFile = new ScoresFile();

        // Retrieve the highest score stored in the file.
        highScore = scoresFile.getHighScore();

        // Spawn the first tetromino in the bag onto the grid.
        grid.setTetromino(generateNextPiece());

        // Hold the next tetromino to be spawned on the grid.
        nextPiece = generateNextPiece();

        // Initialise the remaining lines left to be cleared.
        linesLeft = linesToNextLevel;

    }

    /**
     * Updaes the status of the game model.
     *
     * @param dt the time interval that has passed in seconds.
     */
    public void update(double dt) {

        if (gamePlaying)
            grid.update(dt);
    }

    /**
     * Gets the block colour matrix of the playfield.
     *
     * @return The block colour matrix of the playfield.
     */
    public Color[][] getColourMatrix() {
        return grid.getColourGrid();
    }

    /**
     * Gets the number of lines that have been cleared since the beginning of the game.
     *
     * @return the number of lines cleared.
     */
    public int getNumOfLines() {

        return lines;
    }

    /**
     * Gets the current level of the game.
     *
     * @return the current level of the game.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the player's current score.
     *
     * @return the player's current score.
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Gets the high score of the game.
     *
     * @return the high score of the game.
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Gets the ghost piece of the active tetromino on the playfield
     * @return the ghost piece of the active tetromino on the playfield
     * @since 1.1.0
     */
    public GhostStructure getGhostStructure(){
        return grid.getGhostStructure();
    }

    /**
     * Refills the tetromino bag.
     * <p>
     * The bag is created by placing all the different tetromino types in a list in a random order.
     * The tetrominoes of each type will be generated in that order.
     * </p>
     */
    private void refillBag() {

        // Create a new list containing all tetromino types.
        bag = new ArrayList<>(Arrays.asList(Tetromino.Type.values()));

        // Rearrange them in a random order.
        Collections.shuffle(bag);
    }

    /**
     * Spawns a new tetromino piece from the bag.
     */
    @Nullable
    private Tetromino generateNextPiece() {

        // Refill the bag if it is empty.
        if (bag.size() == 0)
            refillBag();

        // Remove the first type in the bag.
        Tetromino.Type type = bag.remove(0);

        // Generate a new tetromino piece of that type and return it.
        return Tetromino.create(type);
    }

    /**
     * Calculates the gravity speed of the playfield based on the current game level.
     *
     * @return the current gravity speed of the grid.
     */
    float calculateGravity() {

        return (float) Math.pow(0.8f - ((level - 1) * 0.007f), level - 1);
    }

    /**
     * Spawns the next tetromino onto the playfield.
     */
    void setActiveTetromino() {

        // Hold the next tetromino piece to be spawned.
        Tetromino nextTetromino = nextPiece;

        // Set the next tetromino to be held by the Next Queue.
        nextPiece = generateNextPiece();

        // Spawn the held tetromino piece onto the grid.
        grid.setTetromino(nextTetromino);

    }

    /**
     * Gets the structure of the next tetromino piece.
     *
     * @return the structure of the next tetromino piece.
     */
    public ShapeStructure getNextStructure() {

        return nextPiece.getStructure();

    }

    /**
     * Moves the active tetromino piece on the plq6ri3le one space to the left.
     */
    public void shiftTetrominoLeft() {

        if (gamePlaying)
            grid.shiftTetrominoLeft();
    }

    /**
     * Moves the active tetromino piece on the plq6ri3le one space to the right.
     */
    public void shiftTetrominoRight() {

        if (gamePlaying)
            grid.shiftTetrominoRight();
    }

    /**
     * Rotates the active tetromion piece on the playfield 90 degrees clockwise.
     */
    public void rotateClockwise() {

        if (gamePlaying)
            grid.rotateClockwise();
    }

    /**
     * Rotates the active tetromion piece on the playfield 90 degrees anticlockwise.
     */
    public void rotateAnticlockwise() {

        if (gamePlaying)
            grid.rotateAnticlockwise();
    }

    /**
     * Increases the rate at which the active tetromino piece falls down.
     */
    public void activateSoftDrop() {

        if (gamePlaying)
            grid.activateSoftDrop();
    }

    /**
     * Sets the gravity of the active tetrmino piece back to normal.
     */
    public void deactivateSoftDrop() {

        if (gamePlaying)
            grid.deactivateSoftDrop();
    }

    /**
     * Adds a point to the total score due to soft dropping.
     * <p>
     * Should be called for each time the piece falls down one cell due to soft drop.
     * </p>
     */
    void addSoftDropPoints() {

        // Points gained = n, where n is the number of cells fallen while soft dropping.
        currentScore += 1;
    }

    /**
     * Adds a point to the total score due to hard dropping.
     * <p>
     * Should be called for each time the piece falls down one cell due to hard drop.
     * </p>
     */
    void addHardDropPoints() {

        // Points gained = 2n, where n is the number of cells fallen while soft dropping.
        currentScore += 2;
    }

    /**
     * Increases the number of lines cleared on the playfield.
     * <p>
     * The level will be rechecked, and the current score will be updated.
     * </p>
     *
     * @param n the number of lines that were cleared.
     */
    void increaseLines(int n) {

        // Add the lines.
        lines += n;

        // Increase the score based on the number of lines cleared.
        scoreLines(n);

        // Check whether the game should increase the level.
        updateLevel(n);


    }

    /**
     * Takes the number of lines cleared to check whether the the level should increase.
     *
     * @param linesCleared the number of lines that were cleared.
     */
    private void updateLevel(int linesCleared) {

        // Repeat as many times as the number of lines that were cleared.
        for (int i = 0; i < linesCleared; i++) {

            // Decrement the number of lines needed to level up.
            linesLeft -= 1;

            // Check if enough lines have been cleared to level up.
            if (linesLeft == 0) {
                // Increase the level.
                level += 1;
                // Reset the lines needed for the next level.
                linesLeft = linesToNextLevel;

                // Play level up sound effects.
                GameAudio.LEVEL_UP_CLIP.play();
            }
        }
    }

    /**
     * Calculates the points gained from clearing lines, and adds them to the current score.
     *
     * @param linesCleared the number of lines that were cleared.
     */
    private void scoreLines(int linesCleared) {

        // Will hold the score modifier.
        int scoreModifier;

        /*
         * SCORE MODIFIERS:
         *
         * 40   points for 1 line.
         * 100  points for 2 lines.
         * 300  points for 3 lines.
         * 1200 points for 4 lines (maximun possible).
         */
        switch (linesCleared) {

            case 1:
                scoreModifier = 40;
                break;

            case 2:
                scoreModifier = 100;
                break;

            case 3:
                scoreModifier = 300;
                break;

            case 4:
                scoreModifier = 1200;
                break;

            // If the number of points gained is invalid, the result will be zero.
            default:
                scoreModifier = 0;
        }

        // Add the points to the score. The points gained is directly proportional to the current level.
        currentScore += scoreModifier * level;
    }

    void gameOver() {

        gamePlaying = false;
        scoresFile.addNewScore(currentScore);
    }

    /**
     * Applies hard drop to the playfield.
     */
    public void hardDrop() {

        grid.hardDrop();
    }

    /**
     * Checks whether the game is has ended.
     *
     * @return true if the game has ended, false otherwise.
     */
    public boolean checkGameOver() {

        return !gamePlaying;
    }

}

/**
 * Used to retrieve scores from the scores storage file, as well as add new ones.
 *
 * @version 1.0.2
 * @since 1.0.0
 */
class ScoresFile {

    /**
     * The name of the folder where the scores file will be stored.
     */
    private final String DIRECTORY = "data";
    /**
     * The file name of the scores file.
     */
    private final String FILE_NAME = "high-scores.json";
    /**
     * The path to the scores file.
     */
    private final String FILE_PATH = DIRECTORY + "/" + FILE_NAME;
    /**
     * A list containing all the scores stored in the file.
     */
    private List<Integer> scoresList = new ArrayList<>();

    ScoresFile() {

        /* Retrieve all scores from the file */

        // Attempt to read the high-scores file.
        try (FileReader reader = new FileReader(FILE_PATH)) {

            // Create new parser for the scores file.
            JSONParser jsonParser = new JSONParser();

            // Parse the text in the file.
            Object obj = jsonParser.parse(reader);

            // The parsed text will be in the form of an array.
            JSONArray objArray = (JSONArray) obj;

            // Iterate over each element of the array.
            for (Object o : objArray) {

                // Each element will be in the form of a JSONObject.
                JSONObject scoreObj = (JSONObject) o;

                // Get score data from object.
                JSONObject scoreData = (JSONObject) scoreObj.get("scoreData");

                // Get the score.
                Number score = (Number) scoreData.get("score");

                // Add to list.
                scoresList.add(score.intValue());

            }

        }
        // If the high-scores file does not exist:
        catch (IOException | ParseException e) {

            // Create a new high scores file.
            createNewFile();
        }

    }

    /**
     * Creates a new high-scores file if one does not already exist.
     */
    private void createNewFile(){

        // Create new directory to store the score.
        new File(DIRECTORY).mkdir();

        // Attempt to write to high scores file.
        try(FileWriter writer = new FileWriter(FILE_PATH)){

            // Create a new JSONArray.
            JSONArray scoresList = new JSONArray();

            // Write it to the file.
            writer.write(scoresList.toJSONString());
            writer.flush();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Adds new score to the file.
     * @param score the new score to be added.
     */
    void addNewScore(int score) {

        try (FileReader reader = new FileReader(FILE_PATH)) {

            // Create new parser for the scores file.
            JSONParser jsonParser = new JSONParser();

            // Define an identifier for the score using the size of the scores list.
            int id = scoresList.size();

            // Parse the text in the file.
            Object obj = jsonParser.parse(reader);

            // The parsed text will be in the form of an array.
            JSONArray objArray = (JSONArray) obj;

            // Create new JSONObject holding the scores data.
            JSONObject scoreData = new JSONObject();

            // Create new JSONObject for the whole entry.
            JSONObject scoreObject = new JSONObject();

            // Add the ID and the actual score as the scores data.
            scoreData.put("id", id);
            scoreData.put("score", score);

            // Assign the scores data to the object entry.
            scoreObject.put("scoreData", scoreData);

            // Add the entry to the object.
            objArray.add(scoreObject);

            // Create a writer to the file.
            FileWriter writer = new FileWriter(FILE_PATH);
            // Write the updated data to the file.
            writer.write(objArray.toJSONString());
            writer.flush();

        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the highest score stored in the file
     * <p>
     *     0 is returned if there are no stored scores.
     * </p>
     * @return the high score of the game. Returns 0 if there are no scores.
     */
    int getHighScore() {

        // Return zero if the scores list is empty.
        if(scoresList.isEmpty())
            return 0;

        // Otherwise return the highest score in the list.
        return Collections.max(scoresList);
    }
}


