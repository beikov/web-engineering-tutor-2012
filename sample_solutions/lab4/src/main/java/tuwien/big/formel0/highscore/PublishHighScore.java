package tuwien.big.formel0.highscore;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import tuwien.big.formel0.highscore.api.Failure;
import tuwien.big.formel0.highscore.api.GameType;
import tuwien.big.formel0.highscore.api.HighScoreRequestType;
import tuwien.big.formel0.highscore.api.PublishHighScoreEndpoint;
import tuwien.big.formel0.highscore.api.PublishHighScoreService;
import tuwien.big.formel0.highscore.api.TournamentType;
import tuwien.big.formel0.highscore.api.TournamentType.Players.Player;
import tuwien.big.formel0.highscore.api.TournamentType.Rounds.Round;

/**
 * Helper class for publishing the high score results
 *
 * @author pl
 *
 */
public class PublishHighScore {

    private static final String USERKEY = "34EphAp2C4ebaswu";
    private static DatatypeFactory df = null;
    private static PublishHighScoreService service;

    static {
        try {
            df = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException("Could not initialize data type factory", dce);
        }

        service = new PublishHighScoreService();
    }

    private String publishHighScore(HighScoreRequestType in) throws Failure {
        return service.getPublishHighScorePort().publishHighScore(in);
    }

    /**
     * Helper method for accessing the publishing service from the web app
     *
     * @param username
     * @param dateOfBirth
     * @param gender
     * @param winner
     * @param duration
     * @return
     */
    public String publishHighScore(String username, Date dateOfBirth, String gender, String winner, int duration) {

        HighScoreRequestType req = new HighScoreRequestType();
        req.setUserKey(USERKEY);

        //Now in XMLGregoarianCalendar
        GregorianCalendar n = new GregorianCalendar();
        n.setTime(new Date());
        XMLGregorianCalendar now = df.newXMLGregorianCalendar(n);

        TournamentType t = new TournamentType();
        req.setTournament(t);

        //Set three dummy date values
        t.setStartDate(now);
        t.setEndDate(now);
        t.setRegistrationDeadline(now);

        Player p = new Player();

        //Username
        p.setUsername(username);
        //Date of birth
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(dateOfBirth);
        XMLGregorianCalendar date2 = df.newXMLGregorianCalendar(c);
        p.setDateOfBirth(date2);
        //Gender
        p.setGender(gender);

        //Set the player
        TournamentType.Players players = new TournamentType.Players();
        players.getPlayer().add(p);
        t.setPlayers(players);

        //Set the round
        TournamentType.Rounds rounds = new TournamentType.Rounds();
        t.setRounds(rounds);

        Round round = new Round();
        round.setNumber(0);
        rounds.getRound().add(round);

        //Set winnner and duration
        GameType game = new GameType();
        game.setDate(now);
        game.setStatus("finished");
        game.setDuration(BigInteger.valueOf(duration));
        game.setWinner(winner);
        round.getGame().add(game);

        //Set dummy players
        GameType.Players p1 = new GameType.Players();
        game.setPlayers(p1);

        //Publish it
        try {
            return publishHighScore(req);
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }


    }

    /**
     * For debug purposes only
     *
     * @param req
     * @throws Exception
     */
    private void printXML(HighScoreRequestType req) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(HighScoreRequestType.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(req, System.out);
    }
}
