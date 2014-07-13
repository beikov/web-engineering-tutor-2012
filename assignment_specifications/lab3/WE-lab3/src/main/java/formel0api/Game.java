/**
 * <copyright>
 *
 * Copyright (c) 2013 http://www.big.tuwien.ac.at All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * </copyright>
 */
package formel0api;

/**
 * Class representing a Formel 0 game
 */
public class Game {

    private static final int LAST_FIELD = 6;
    /**
     * Player playing the game
     */
    private Player player;
    /**
     * Computer opponent
     */
    private Player computer;
    /**
     * Dice that is used in this game
     */
    private Dice dice = new Dice();
    /**
     * Specifies if the game is over (
     * <code>true</code>) or not (
     * <code>false</code)
     */
    private boolean gameOver = false;
    /**
     * Starting time of the game
     */
    private long gamestarttime = System.currentTimeMillis();
    /**
     * Time already spent in this game
     */
    private long spenttime;

    /**
     * Constructs a new {@link Game}
     */
    public Game(Player player, Player computer) {
        this.player = player;
        this.computer = computer;
    }

    /**
     * Specifies whether this game is over or not
     *
     * @return <code>true</code> if this game is over, <code>false</code>
     * otherwise
     */
    public boolean isGameOver() {
        return this.gameOver;
    }

    /**
     * Returns the time already spent on this game
     *
     * @return the time already spent on this game
     */
    public long getSpentTime() {
        if (!gameOver) {
            spenttime = System.currentTimeMillis() - this.gamestarttime;
        }
        return spenttime;
    }

    /**
     * Rolls the dice for the player and updates the position of the player's
     * car according to the score
     *
     * @param player Player who rolls the dice
     * @return score
     */
    public int rollthedice(Player player) {
        if (gameOver) {
            throw new IllegalArgumentException(
                    "Game is over. Rolling the dice is not allowed.");
        }

        int score = dice.roll();

        int position = player.getPosition();

        /**
         * Move on field
         */
        int newposition = Math.min(position + score, LAST_FIELD);
        player.setPosition(newposition);

        /**
         * Test if deadly field was reached
         */
        if (newposition == 2 || newposition == 5) {
            newposition = 0;
            player.setPosition(newposition);
        }

        /**
         * Test if the figure of the player reached the end and the game is over
         */
        if (newposition == LAST_FIELD) { // player reached end
            gameOver = true;
        }

        return score;
    }

    /**
     * Returns the currently leading player
     *
     * @return the currently leading player
     */
    public Player getLeader() {
        if (player.getPosition() > computer.getPosition()) {
            return player;
        } else if (computer.getPosition() > player.getPosition()) {
            return computer;
        } else {
            return null;
        }
    }

    /**
     * Returns the player
     *
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the computer
     *
     * @return computer
     */
    public Player getComputer() {
        return computer;
    }
}