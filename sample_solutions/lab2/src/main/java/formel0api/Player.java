/**
 * <copyright>
 *
 * Copyright (c) 2010 http://www.big.tuwien.ac.at All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * </copyright>
 */
package formel0api;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a player playing in a {@link Game}.
 */
public class Player {

    /**
     * History of the positions of the player
     */
    private List<Integer> history = new ArrayList<Integer>();
    /**
     * The name of this user
     */
    private String name;
    /**
     * The current position of the user's car
     */
    private int position = 0;

    /**
     * Initializes a {@link Player} with the specified
     * <code>name</code>.
     *
     * @param name to set
     */
    public Player(String name) {
        super();
        this.name = name;
        setPosition(0);
    }

    /**
     * Returns the name of this player.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the actual position of this player's car
     *
     * @return the actual position of this player's car
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Sets the actual position of this player's car
     *
     * @param pos actual position of this player's car
     */
    public void setPosition(int pos) {
        this.position = pos;
        history.add(new Integer(pos));
    }

    /**
     * Returns the position at time (now - t) (i.e., if t=0 returns the current
     * position, if t=1 returns last position, etc.)
     *
     * @param t position at time (now - t)
     */
    public int getPositionMinusT(int t) {
        int index = history.size() - 1 - t;
        if (index >= 0 && index < history.size()) {
            return history.get(index);
        }
        return -1;
    }
}
