package at.ac.tuwien.big.testsuite.impl.selenium;

/*
 * Copyright 2013 BIG TU Wien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import static at.ac.tuwien.big.testsuite.impl.selenium.BaseSeleniumTest.textById;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Christian, Florian Zoubek
 */
public class TableTest extends BaseSeleniumTest {

    private static final Pattern DICE_VALUE_PATTERN = Pattern.compile(".+wuerfel([0-3])\\.png.*");
    private static final int NO_MOVEMENT_ROUNDS_THRESHOLD = 2;
    private static final int MAX_TRIES = 100;
    private static final int MAX_MOVES = 100;
    private static final Integer[] allowedDiceValues = new Integer[]{1, 2, 3};
    private boolean jumpedOnOilyField = false;
    private boolean onceWon = false;
    private boolean onceLost = false;
    private boolean multipleTested = false;
    private String diceFormPrefix = "";
    private String navigationFormPrefix = "";

    private void checkInitialState(WebDriver driver, String title) throws Exception {
        checkThat(title + ": Initial leader is wrong", getLeader(driver), equalTo("mehrere"));
        checkThat(title + ": Initial round is wrong", getRound(driver), equalTo(1));
        checkThat(title + ": Initial time is wrong", getTime(driver), equalTo("00:00"));
        checkThat(title + ": Initial computer score is wrong", getComputerScore(driver), equalTo(0));
        checkThat(title + ": Initial dice value is wrong", getDiceValue(driver), equalTo(0));
        checkThat(title + ": Initial current player name is wrong", getCurrentPlayerName(driver), equalTo(user));
        checkThat(title + ": Initial player position of player 1 is wrong", getPlayerPosition(driver, 1), equalTo(0));
        checkThat(title + ": Initial player position of player 2 is wrong", getPlayerPosition(driver, 2), equalTo(0));
        checkThat(title + ": Field 2 is not oily but it should be", isOily(driver, 2), is(true));
        checkThat(title + ": Field 5 is not oily but it should be", isOily(driver, 5), is(true));
    }

    /**
     * Gameplay test - tests and simulates multiple game passes and exports the
     * page content at startup, after the first step and after a player has won
     * for the first time. Performed checks: - player and computer movement -
     * round count - leader - oily field reachable - player wins - computer wins
     * - restart
     *
     * @throws Exception
     */
    @Test
    public void testFeatures_shouldVerifyEveryFeatureAtLeastOnce() throws Exception {
        exportCurrentHTML(driver, "startup.html");
        boolean firstStepExportDone = false;
        boolean finishExportDone = false;

        checkInitialState(driver, "Normal test");

        OUTER:
        for (int i = 0; i < MAX_TRIES && (!jumpedOnOilyField || !onceLost || !onceWon); i++) {
            boolean finished = false;
            int noMovementRounds = 0;
            Integer expectedPosition = 0;
            Integer expectedComputerPosition = 0;
            Integer expectedRound = getRound(driver);

            checkThat("Round is wrong", expectedRound, isIn(Arrays.asList(0, 1)));

            if (expectedRound == null) {
                checkThat("Unexpected value for round", expectedRound, notNullValue());
                expectedRound = 1;
            }

            for (int n = 0; n < MAX_MOVES && !finished; n++) {
                if (noMovementRounds >= NO_MOVEMENT_ROUNDS_THRESHOLD) {
                    checkThat("Can't play any more since the dice has either been disabled or can't be clicked anymore", true, is(false));
                    break OUTER;
                }

                Integer diceValue = rollDice(driver);
                Integer computerDiceValue = getComputerScore(driver);

                assertNotNull(TestsuiteConstants.KNOWN_ERROR_PREFIX+" Unexpected value for diceValue", diceValue);
                assertNotNull(TestsuiteConstants.KNOWN_ERROR_PREFIX+" Unexpected value for computerDiceValue", computerDiceValue);

                expectedRound++;

                // check dice value
                checkThat("Dice value '" + diceValue + "' is none of the allowed values {1, 2, 3}", diceValue, isIn(allowedDiceValues));

                // check player move

                expectedPosition += diceValue;
                if (expectedPosition > 0 && expectedPosition < 6 && isOily(driver, expectedPosition) != null && isOily(driver, expectedPosition)) {
                    jumpedOnOilyField = true;
                    expectedPosition = 0;
                }
                if (expectedPosition > 6) {
                    expectedPosition = 6;
                }

                // check computer move

                expectedComputerPosition += computerDiceValue;
                if (expectedComputerPosition > 0 && expectedComputerPosition < 6 && isOily(driver, expectedComputerPosition) != null && isOily(driver, expectedComputerPosition)) {
                    jumpedOnOilyField = true;
                    expectedComputerPosition = 0;
                }
                if (expectedComputerPosition > 6) {
                    expectedComputerPosition = 6;
                }

                // Wait for the elements to appear in the expected positions
                if (!waitForJQuery(driver, By.xpath("//li[@id='" + getFieldId(expectedPosition) + "']//span[@id='player1']"))) {
                    checkThat("Could not retrieve position of player 1 by id 'player1' which might be caused by a concurrency error", true, is(false));
                }
                if (!waitForJQuery(driver, By.xpath("//li[@id='" + getFieldId(expectedComputerPosition) + "']//span[@id='player2']"))) {
                    checkThat("Could not retrieve position of player 2 by id 'player2' which might be caused by a concurrency error", true, is(false));
                }

                // check round
                Integer round = getRound(driver);
                assertNotNull(TestsuiteConstants.KNOWN_ERROR_PREFIX+" Unexpected value for round", round);
                checkThat("Round count of " + round + " is not as expected (" + expectedRound + ")", round, is(expectedRound));

                if (!firstStepExportDone) {
                    exportCurrentHTML(driver, "first_step.html");
                    firstStepExportDone = true;
                }

                Integer position = getPlayerPosition(driver, 1);
                Integer computerPosition = getPlayerPosition(driver, 2);

                assertNotNull(TestsuiteConstants.KNOWN_ERROR_PREFIX+" Unexpected value for position", position);
                assertNotNull(TestsuiteConstants.KNOWN_ERROR_PREFIX+" Unexpected value for computerPosition", computerPosition);

                checkThat("Incorrect Player position: " + position + " (expected: " + expectedPosition + ")", position, is(expectedPosition));
                checkThat("Incorrect Computer position: " + computerPosition + " (expected: " + expectedComputerPosition + ")", computerPosition, is(expectedComputerPosition));

                if (((position == expectedPosition - diceValue && isOily(driver, position + diceValue) != null && !isOily(driver, position + diceValue)) || (expectedPosition == 0 && isOily(driver, position + diceValue) != null && isOily(driver, position + diceValue)))
                        && ((computerPosition == expectedComputerPosition - computerDiceValue && isOily(driver, computerPosition + computerDiceValue) != null && !isOily(driver, computerPosition + computerDiceValue)) || (expectedComputerPosition == 0 && isOily(driver, computerPosition + computerDiceValue) != null && isOily(driver, computerPosition + computerDiceValue)))
                        && round == expectedRound - 1) {
                    noMovementRounds++;
                }

                // do this so that even if students do something wrong, we don't have to wait for coming timeouts
                expectedPosition = position;
                expectedComputerPosition = computerPosition;
                expectedRound = round;

                // check leader

                String expectedLeader = "mehrere";
                if (position > computerPosition) {
                    expectedLeader = getPlayerName(driver, 1).toLowerCase();
                } else if (position < computerPosition) {
                    expectedLeader = getPlayerName(driver, 2).toLowerCase();
                }
                String leader = getLeader(driver);
                checkThat("Incorrect Leader: " + leader + " (expected: " + expectedLeader + ") because player position is " + position + " and computer position is " + computerPosition, leader, is(expectedLeader));

                if (position == 6) {
                    onceWon = true;
                    finished = true;
                }
                if (computerPosition == 6) {
                    onceLost = true;
                    finished = true;
                }
                if (!multipleTested && (position != 0 || computerPosition != 0)) {
                    multipleTested = true;
                    WebDriver secondDriver = null;

                    try {
                        secondDriver = createDriver();
                        checkInitialState(secondDriver, "Concurrent test");
                    } finally {
                        if (secondDriver != null) {
                            secondDriver.quit();
                        }
                    }
                }
            }

            if (!finishExportDone && finished) {
                exportCurrentHTML(driver, "finish.html");
                finishExportDone = true;
            }

            // try to restart

            startNewGame(driver);

            Integer player1Position = getPlayerPosition(driver, 1);
            Integer player2Position = getPlayerPosition(driver, 2);

            checkThat("Player position not 0 after restart", player1Position, is(0));
            checkThat("Computer position not 0 after restart", player2Position, is(0));

            if (player1Position == null || player1Position != 0 || player2Position == null || player2Position != 0) {
                break;
            }
        }

        checkThat("Oily field never reached", jumpedOnOilyField, is(true));
        checkThat("Concurrent test could not be executed", multipleTested, is(true));
        checkThat("Player couldn't win at least once after " + MAX_TRIES + " games", onceLost, is(true));
        checkThat("Computer couldn't win at least once after " + MAX_TRIES + " games", onceWon, is(true));
    }

    private String getPlayerName(WebDriver driver, int playerNr) {
        return textById(driver, "player" + playerNr + "Name");
    }

    private String getLeader(WebDriver driver) {
        String leader = textById(driver, "leader");
        return leader == null ? null : leader.toLowerCase();
    }

    private Integer getRound(WebDriver driver) {
        String round = textById(driver, "round");

        try {
            return round == null ? null : Integer.parseInt(round.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String getTime(WebDriver driver) {
        String time = textById(driver, "time");
        return "0:00".equals(time) || "0:00\"".equals(time) || "00:00\"".equals(time) ? "00:00" : time;
    }

    private Integer getComputerScore(WebDriver driver) {
        String computerScore = textById(driver, "computerScore");

        try {
            return computerScore == null ? null : Integer.parseInt(computerScore.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer getDiceValue(WebDriver driver) {
        String src = null;

        if (exists(driver, By.id(diceFormPrefix + "diceImage"))) {
            src = driver.findElement(By.id(diceFormPrefix + "diceImage")).getAttribute("src");
        } else if (exists(driver, By.id(diceFormPrefix + "dice"))) {
            WebElement diceElement = driver.findElement(By.id(diceFormPrefix + "dice"));
            src = diceElement.getAttribute("src");

            if (src == null) {
                List<WebElement> imgElems = diceElement.findElements(By.xpath(".//img"));

                if (!imgElems.isEmpty()) {
                    diceElement = imgElems.get(0);
                    src = diceElement.getAttribute("src");
                }
            }
        }

        if (src == null) {
            assertFalse(TestsuiteConstants.KNOWN_ERROR_PREFIX+" Could not retrieve dice value", true);
        }

        Matcher matcher = DICE_VALUE_PATTERN.matcher(src);

        if (!matcher.matches()) {
            assertFalse(TestsuiteConstants.KNOWN_ERROR_PREFIX+" Could not retrieve dice value", true);
        }

        return Integer.parseInt(matcher.group(1));
    }

    private String getCurrentPlayerName(WebDriver driver) {
        return textById(driver, "currentPlayerName");
    }

    private Integer getPlayerPosition(WebDriver driver, int playerNumber) {
        WebElement parentElement = getParentElement(driver, By.id("player" + playerNumber));

        if (parentElement == null) {
            return null;
        }

        String id = parentElement.getAttribute("id");

        if ("start_road".equals(id)) {
            return 0;
        } else if ("finish_road".equals(id)) {
            return 6;
        } else if (id.startsWith("road_")) {
            return Integer.parseInt(id.substring("road_".length()));
        } else {
            return null;
        }
    }

    private String getFieldId(int position) {
        if (position == 0) {
            return "start_road";
        } else if (position == 6) {
            return "finish_road";
        } else if (position > 0 && position < 6) {
            return "road_" + position;
        } else {
            return null;
        }
    }

    private Boolean isOily(WebDriver driver, int roadId) {
        WebElement road = driver.findElement(By.id("road_" + roadId));
        return road == null ? null : road.getAttribute("class").contains("oil_road");
    }

    private void startNewGame(WebDriver driver) {
        if (exists(By.id("startNewGame"))) {
            clickNonAjax(driver, By.id("startNewGame"));
            waitForJQuery(driver);
        } else if (exists(By.id(navigationFormPrefix + "startNewGame"))) {
            clickNonAjax(driver, By.id(navigationFormPrefix + "startNewGame"));
            waitForJQuery(driver);
        } else {
            checkThat("The start new game link does not exist", true, is(false));
        }
    }

    private Integer rollDice(WebDriver driver) {
        By diceId = By.id(diceFormPrefix + "dice");
        clickNonAjax(driver, diceId);
        waitForJQuery(driver);
        return getDiceValue(driver);
    }

    public String getDiceFormPrefix() {
        return diceFormPrefix;
    }

    public void setDiceFormPrefix(String diceFormPrefix) {
        this.diceFormPrefix = diceFormPrefix;
    }

    public String getNavigationFormPrefix() {
        return navigationFormPrefix;
    }

    public void setNavigationFormPrefix(String navigationFormPrefix) {
        this.navigationFormPrefix = navigationFormPrefix;
    }
}