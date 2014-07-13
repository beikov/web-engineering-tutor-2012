package tuwien.big.formel0.highscore.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>Java class for gameType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType name="gameType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="players">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="player" maxOccurs="2" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="game-history" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="move" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="player" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="dots" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                 &lt;minInclusive value="1"/>
 *                                 &lt;maxInclusive value="6"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="start-point" use="required" type="{http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament}gameField" />
 *                           &lt;attribute name="end-point" use="required" type="{http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament}gameField" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="date" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="status" default="planned">
 *         &lt;simpleType>
 *           &lt;union>
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                 &lt;minInclusive value="0"/>
 *                 &lt;maxInclusive value="3"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                 &lt;enumeration value="planned"/>
 *                 &lt;enumeration value="finished"/>
 *                 &lt;enumeration value="stopped"/>
 *                 &lt;enumeration value="running"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/union>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="duration" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="winner" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gameType", propOrder = {
    "players",
    "gameHistory"
})
public class GameType {

    @XmlElement(required = true)
    protected GameType.Players players;
    @XmlElement(name = "game-history")
    protected GameType.GameHistory gameHistory;
    @XmlAttribute(name = "date", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;
    @XmlAttribute(name = "status")
    protected String status;
    @XmlAttribute(name = "duration")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger duration;
    @XmlAttribute(name = "winner")
    protected String winner;

    /**
     * Gets the value of the players property.
     *
     * @return possible object is {@link GameType.Players }
     *
     */
    public GameType.Players getPlayers() {
        return players;
    }

    /**
     * Sets the value of the players property.
     *
     * @param value allowed object is {@link GameType.Players }
     *
     */
    public void setPlayers(GameType.Players value) {
        this.players = value;
    }

    /**
     * Gets the value of the gameHistory property.
     *
     * @return possible object is {@link GameType.GameHistory }
     *
     */
    public GameType.GameHistory getGameHistory() {
        return gameHistory;
    }

    /**
     * Sets the value of the gameHistory property.
     *
     * @param value allowed object is {@link GameType.GameHistory }
     *
     */
    public void setGameHistory(GameType.GameHistory value) {
        this.gameHistory = value;
    }

    /**
     * Gets the value of the date property.
     *
     * @return possible object is {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     *
     * @param value allowed object is {@link XMLGregorianCalendar }
     *
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getStatus() {
        if (status == null) {
            return "planned";
        } else {
            return status;
        }
    }

    /**
     * Sets the value of the status property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the duration property.
     *
     * @return possible object is {@link BigInteger }
     *
     */
    public BigInteger getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     *
     * @param value allowed object is {@link BigInteger }
     *
     */
    public void setDuration(BigInteger value) {
        this.duration = value;
    }

    /**
     * Gets the value of the winner property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getWinner() {
        return winner;
    }

    /**
     * Sets the value of the winner property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setWinner(String value) {
        this.winner = value;
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained
     * within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="move" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="player" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="dots" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                       &lt;minInclusive value="1"/>
     *                       &lt;maxInclusive value="6"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="start-point" use="required" type="{http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament}gameField" />
     *                 &lt;attribute name="end-point" use="required" type="{http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament}gameField" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "move"
    })
    public static class GameHistory {

        protected List<GameType.GameHistory.Move> move;

        /**
         * Gets the value of the move property.
         *
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the move property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMove().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link GameType.GameHistory.Move }
         *
         *
         */
        public List<GameType.GameHistory.Move> getMove() {
            if (move == null) {
                move = new ArrayList<GameType.GameHistory.Move>();
            }
            return this.move;
        }

        /**
         * <p>Java class for anonymous complex type.
         *
         * <p>The following schema fragment specifies the expected content
         * contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="player" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="dots" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *             &lt;minInclusive value="1"/>
         *             &lt;maxInclusive value="6"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="start-point" use="required" type="{http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament}gameField" />
         *       &lt;attribute name="end-point" use="required" type="{http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament}gameField" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Move {

            @XmlAttribute(name = "player", required = true)
            protected String player;
            @XmlAttribute(name = "dots", required = true)
            protected int dots;
            @XmlAttribute(name = "start-point", required = true)
            protected int startPoint;
            @XmlAttribute(name = "end-point", required = true)
            protected int endPoint;

            /**
             * Gets the value of the player property.
             *
             * @return possible object is {@link String }
             *
             */
            public String getPlayer() {
                return player;
            }

            /**
             * Sets the value of the player property.
             *
             * @param value allowed object is {@link String }
             *
             */
            public void setPlayer(String value) {
                this.player = value;
            }

            /**
             * Gets the value of the dots property.
             *
             */
            public int getDots() {
                return dots;
            }

            /**
             * Sets the value of the dots property.
             *
             */
            public void setDots(int value) {
                this.dots = value;
            }

            /**
             * Gets the value of the startPoint property.
             *
             */
            public int getStartPoint() {
                return startPoint;
            }

            /**
             * Sets the value of the startPoint property.
             *
             */
            public void setStartPoint(int value) {
                this.startPoint = value;
            }

            /**
             * Gets the value of the endPoint property.
             *
             */
            public int getEndPoint() {
                return endPoint;
            }

            /**
             * Sets the value of the endPoint property.
             *
             */
            public void setEndPoint(int value) {
                this.endPoint = value;
            }
        }
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained
     * within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="player" maxOccurs="2" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "player"
    })
    public static class Players {

        protected List<GameType.Players.Player> player;

        /**
         * Gets the value of the player property.
         *
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the player property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPlayer().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link GameType.Players.Player }
         *
         *
         */
        public List<GameType.Players.Player> getPlayer() {
            if (player == null) {
                player = new ArrayList<GameType.Players.Player>();
            }
            return this.player;
        }

        /**
         * <p>Java class for anonymous complex type.
         *
         * <p>The following schema fragment specifies the expected content
         * contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Player {

            @XmlAttribute(name = "ref")
            protected String ref;

            /**
             * Gets the value of the ref property.
             *
             * @return possible object is {@link String }
             *
             */
            public String getRef() {
                return ref;
            }

            /**
             * Sets the value of the ref property.
             *
             * @param value allowed object is {@link String }
             *
             */
            public void setRef(String value) {
                this.ref = value;
            }
        }
    }
}
