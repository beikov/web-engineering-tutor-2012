package tuwien.big.formel0.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import formel0api.Game;
import formel0api.Player;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tuwien.big.formel0.highscore.PublishHighScore;
import tuwien.big.formel0.twitter.ITwitterClient;
import tuwien.big.formel0.twitter.TwitterClient;
import tuwien.big.formel0.twitter.TwitterStatusMessage;
import tuwien.big.formel0.utilities.Utility;

@ManagedBean(name = "gc")
@SessionScoped
public class GameControl {

    Player player;
    Player computer;
    Game game;
    int playerscore;
    int computerscore;
    int round = 1;
    String playername;
    Date birthday;
    String sex;

    public GameControl() {
        this("Susi");
    }

    public GameControl(String playername) {
        this(playername, null, null);
    }

    /**
     * Initializes a new game.
     */
    public GameControl(String playername, Date birthday, String sex) {
        this.playername = playername;
        this.birthday = birthday;
        this.sex = sex;
        init();
    }

    public void init() {
        player = new Player(playername);
        computer = new Player("Deep Blue");
        playerscore = 0;
        computerscore = 0;
        this.game = new Game(player, computer);
        round = 1;
    }

    /**
     * Returns the time already spent on this game
     *
     * @return the time already spent on this game
     */
    public String getTime() {
        long milliseconds = game.getSpentTime();
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                (TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))));
    }

    /**
     * Specifies whether this game is over or not
     *
     * @return <code>true</code> if this game is over, <code>false</code>
     * otherwise.
     */
    public boolean isGameOver() {
        return game.isGameOver();
    }

    /**
     * Returns the rounds already played in this game
     *
     * @return the rounds already played in this game
     */
    public int getRound() {
        return round;
    }

    /**
     * Returns the currently leading player
     *
     * @return the currently leading player
     */
    public Player getLeader() {
        return game.getLeader();
    }

    /**
     * Rolls the dice for the player
     */
    public void doRound() {
        if (isGameOver()) {
            return;
        }

        this.playerscore = game.rollthedice(player);

        if (!isGameOver()) {
            this.computerscore = game.rollthedice(computer);
            
            if(game.isGameOver()) {
                publish();
            }
        } else {
            publish();
            this.computerscore = 0;
        }

        ++round;
    }
    
    private void publish(){
        String key = new PublishHighScore().publishHighScore(playername, birthday, sex, game.getLeader().getName(), (int) game.getSpentTime());

        if (key.startsWith("ERROR")) {
            // Could not publish highscore
            Logger.getLogger(GameControl.class.getName()).log(Level.SEVERE, key);
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", Utility.getResourceText(ctx, "msg", "couldnotpublish"));
            ctx.addMessage("info", message);
        } else {
            Logger.getLogger(GameControl.class.getName()).log(Level.INFO, key);

            try {
                ITwitterClient twitter = new TwitterClient();
                twitter.publishUuid(new TwitterStatusMessage(playername, key, new Date()));

                FacesContext ctx = FacesContext.getCurrentInstance();
                String text = Utility.getResourceText(ctx, "msg", "uuidtweeted");
                text = MessageFormat.format(text, key);
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", text);
                ctx.addMessage("info", message);
            } catch (Exception ex) {
                Logger.getLogger(GameControl.class.getName()).log(Level.SEVERE, null, ex);
                // Could not tweet the UUID
                FacesContext ctx = FacesContext.getCurrentInstance();
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", Utility.getResourceText(ctx, "msg", "couldnottweet"));
                ctx.addMessage("info", message);
            }
        }
    }

    /**
     * Returns the score thrown by the player
     *
     * @return the score thrown by the player
     */
    public String getDiceResource() {
        return "img:wuerfel" + getPlayerScore() + ".png";
    }

    /**
     * Returns the score thrown by the player
     *
     * @return the score thrown by the player
     */
    public int getPlayerScore() {
        return this.playerscore;
    }

    /**
     * Returns the score of the computer
     *
     * @return the score of the computer-controlled opponent
     */
    public int getComputerScore() {
        return this.computerscore;
    }

    /**
     * Returns player 1 of the game
     *
     * @return player 1 of the game
     */
    public Player getPlayer1() {
        return this.player;
    }

    /**
     * Return player 2 of the game
     *
     * @return player 2 of the game
     */
    public Player getPlayer2() {
        return this.computer;
    }
}