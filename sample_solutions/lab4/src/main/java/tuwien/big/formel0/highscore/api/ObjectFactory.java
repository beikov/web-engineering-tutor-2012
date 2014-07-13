package tuwien.big.formel0.highscore.api;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the tuwien.big.formel0.highscore.api package.
 * <p>An ObjectFactory allows you to programatically construct new instances of
 * the Java representation for XML content. The Java representation of XML
 * content can consist of schema derived interfaces and classes representing the
 * binding of schema type definitions, element declarations and model groups.
 * Factory methods for each of these are provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _HighScoreRequest_QNAME = new QName("http://big.tuwien.ac.at/we/highscore/data", "HighScoreRequest");
    private final static QName _Tournament_QNAME = new QName("http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament", "tournament");
    private final static QName _HighScoreResponse_QNAME = new QName("http://big.tuwien.ac.at/we/highscore/data", "HighScoreResponse");
    private final static QName _UserKey_QNAME = new QName("http://big.tuwien.ac.at/we/highscore/data", "UserKey");
    private final static QName _Failure_QNAME = new QName("http://big.tuwien.ac.at/we/highscore", "Failure");
    private final static QName _DescriptionTypeP_QNAME = new QName("http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament", "p");
    private final static QName _DescriptionTypeEm_QNAME = new QName("http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament", "em");

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: tuwien.big.formel0.highscore.api
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GameType }
     *
     */
    public GameType createGameType() {
        return new GameType();
    }

    /**
     * Create an instance of {@link GameType.GameHistory }
     *
     */
    public GameType.GameHistory createGameTypeGameHistory() {
        return new GameType.GameHistory();
    }

    /**
     * Create an instance of {@link GameType.Players }
     *
     */
    public GameType.Players createGameTypePlayers() {
        return new GameType.Players();
    }

    /**
     * Create an instance of {@link TournamentType }
     *
     */
    public TournamentType createTournamentType() {
        return new TournamentType();
    }

    /**
     * Create an instance of {@link TournamentType.Rounds }
     *
     */
    public TournamentType.Rounds createTournamentTypeRounds() {
        return new TournamentType.Rounds();
    }

    /**
     * Create an instance of {@link TournamentType.Players }
     *
     */
    public TournamentType.Players createTournamentTypePlayers() {
        return new TournamentType.Players();
    }

    /**
     * Create an instance of {@link FailureType }
     *
     */
    public FailureType createFailureType() {
        return new FailureType();
    }

    /**
     * Create an instance of {@link HighScoreRequestType }
     *
     */
    public HighScoreRequestType createHighScoreRequestType() {
        return new HighScoreRequestType();
    }

    /**
     * Create an instance of {@link DescriptionType }
     *
     */
    public DescriptionType createDescriptionType() {
        return new DescriptionType();
    }

    /**
     * Create an instance of {@link GameType.GameHistory.Move }
     *
     */
    public GameType.GameHistory.Move createGameTypeGameHistoryMove() {
        return new GameType.GameHistory.Move();
    }

    /**
     * Create an instance of {@link GameType.Players.Player }
     *
     */
    public GameType.Players.Player createGameTypePlayersPlayer() {
        return new GameType.Players.Player();
    }

    /**
     * Create an instance of {@link TournamentType.Rounds.Round }
     *
     */
    public TournamentType.Rounds.Round createTournamentTypeRoundsRound() {
        return new TournamentType.Rounds.Round();
    }

    /**
     * Create an instance of {@link TournamentType.Players.Player }
     *
     */
    public TournamentType.Players.Player createTournamentTypePlayersPlayer() {
        return new TournamentType.Players.Player();
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link HighScoreRequestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://big.tuwien.ac.at/we/highscore/data", name = "HighScoreRequest")
    public JAXBElement<HighScoreRequestType> createHighScoreRequest(HighScoreRequestType value) {
        return new JAXBElement<HighScoreRequestType>(_HighScoreRequest_QNAME, HighScoreRequestType.class, null, value);
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link TournamentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament", name = "tournament")
    public JAXBElement<TournamentType> createTournament(TournamentType value) {
        return new JAXBElement<TournamentType>(_Tournament_QNAME, TournamentType.class, null, value);
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://big.tuwien.ac.at/we/highscore/data", name = "HighScoreResponse")
    public JAXBElement<String> createHighScoreResponse(String value) {
        return new JAXBElement<String>(_HighScoreResponse_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://big.tuwien.ac.at/we/highscore/data", name = "UserKey")
    public JAXBElement<String> createUserKey(String value) {
        return new JAXBElement<String>(_UserKey_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link FailureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://big.tuwien.ac.at/we/highscore", name = "Failure")
    public JAXBElement<FailureType> createFailure(FailureType value) {
        return new JAXBElement<FailureType>(_Failure_QNAME, FailureType.class, null, value);
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link DescriptionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament", name = "p", scope = DescriptionType.class)
    public JAXBElement<DescriptionType> createDescriptionTypeP(DescriptionType value) {
        return new JAXBElement<DescriptionType>(_DescriptionTypeP_QNAME, DescriptionType.class, DescriptionType.class, value);
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament", name = "em", scope = DescriptionType.class)
    public JAXBElement<String> createDescriptionTypeEm(String value) {
        return new JAXBElement<String>(_DescriptionTypeEm_QNAME, String.class, DescriptionType.class, value);
    }
}
