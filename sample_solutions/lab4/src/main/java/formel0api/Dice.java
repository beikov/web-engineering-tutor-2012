package formel0api;

import java.util.Random;

/**
 * Class representing a dice
 */
public class Dice {

    /**
     * Object used to generate pseudo-random generated numbers that represent
     * the score thrown by rolling this dice
     */
    Random random;

    /**
     * Creates a new dice
     */
    public Dice() {
        random = new Random();
    }

    /**
     * Rolls the dice, i.e., specifies the score thrown using the dice
     *
     * @return score thrown
     */
    public int roll() {
        return random.nextInt(3) + 1;
    }
}
