package tuwien.big.formel0.highscore.api;

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
 * <p>Java class for tournamentType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType name="tournamentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="players">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="player" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="date-of-birth" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                             &lt;element name="gender">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;enumeration value="MALE"/>
 *                                   &lt;enumeration value="FEMALE"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                           &lt;/all>
 *                           &lt;attribute name="username" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="rounds">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="round" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="game" type="{http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament}gameType" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="number" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="description" type="{http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament}descriptionType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="start-date" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="end-date" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="registration-deadline" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tournamentType", propOrder = {
    "players",
    "rounds",
    "description"
})
public class TournamentType {

    @XmlElement(required = true)
    protected TournamentType.Players players;
    @XmlElement(required = true)
    protected TournamentType.Rounds rounds;
    protected DescriptionType description;
    @XmlAttribute(name = "start-date", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar startDate;
    @XmlAttribute(name = "end-date", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar endDate;
    @XmlAttribute(name = "registration-deadline", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar registrationDeadline;

    /**
     * Gets the value of the players property.
     *
     * @return possible object is {@link TournamentType.Players }
     *
     */
    public TournamentType.Players getPlayers() {
        return players;
    }

    /**
     * Sets the value of the players property.
     *
     * @param value allowed object is {@link TournamentType.Players }
     *
     */
    public void setPlayers(TournamentType.Players value) {
        this.players = value;
    }

    /**
     * Gets the value of the rounds property.
     *
     * @return possible object is {@link TournamentType.Rounds }
     *
     */
    public TournamentType.Rounds getRounds() {
        return rounds;
    }

    /**
     * Sets the value of the rounds property.
     *
     * @param value allowed object is {@link TournamentType.Rounds }
     *
     */
    public void setRounds(TournamentType.Rounds value) {
        this.rounds = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return possible object is {@link DescriptionType }
     *
     */
    public DescriptionType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is {@link DescriptionType }
     *
     */
    public void setDescription(DescriptionType value) {
        this.description = value;
    }

    /**
     * Gets the value of the startDate property.
     *
     * @return possible object is {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     *
     * @param value allowed object is {@link XMLGregorianCalendar }
     *
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     *
     * @return possible object is {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     *
     * @param value allowed object is {@link XMLGregorianCalendar }
     *
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the registrationDeadline property.
     *
     * @return possible object is {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getRegistrationDeadline() {
        return registrationDeadline;
    }

    /**
     * Sets the value of the registrationDeadline property.
     *
     * @param value allowed object is {@link XMLGregorianCalendar }
     *
     */
    public void setRegistrationDeadline(XMLGregorianCalendar value) {
        this.registrationDeadline = value;
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
     *         &lt;element name="player" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;all>
     *                   &lt;element name="date-of-birth" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *                   &lt;element name="gender">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;enumeration value="MALE"/>
     *                         &lt;enumeration value="FEMALE"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                 &lt;/all>
     *                 &lt;attribute name="username" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
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

        protected List<TournamentType.Players.Player> player;

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
         * {@link TournamentType.Players.Player }
         *
         *
         */
        public List<TournamentType.Players.Player> getPlayer() {
            if (player == null) {
                player = new ArrayList<TournamentType.Players.Player>();
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
         *       &lt;all>
         *         &lt;element name="date-of-birth" type="{http://www.w3.org/2001/XMLSchema}date"/>
         *         &lt;element name="gender">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;enumeration value="MALE"/>
         *               &lt;enumeration value="FEMALE"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *       &lt;/all>
         *       &lt;attribute name="username" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {})
        public static class Player {

            @XmlElement(name = "date-of-birth", required = true)
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar dateOfBirth;
            @XmlElement(required = true)
            protected String gender;
            @XmlAttribute(name = "username", required = true)
            protected String username;

            /**
             * Gets the value of the dateOfBirth property.
             *
             * @return possible object is {@link XMLGregorianCalendar }
             *
             */
            public XMLGregorianCalendar getDateOfBirth() {
                return dateOfBirth;
            }

            /**
             * Sets the value of the dateOfBirth property.
             *
             * @param value allowed object is {@link XMLGregorianCalendar }
             *
             */
            public void setDateOfBirth(XMLGregorianCalendar value) {
                this.dateOfBirth = value;
            }

            /**
             * Gets the value of the gender property.
             *
             * @return possible object is {@link String }
             *
             */
            public String getGender() {
                return gender;
            }

            /**
             * Sets the value of the gender property.
             *
             * @param value allowed object is {@link String }
             *
             */
            public void setGender(String value) {
                this.gender = value;
            }

            /**
             * Gets the value of the username property.
             *
             * @return possible object is {@link String }
             *
             */
            public String getUsername() {
                return username;
            }

            /**
             * Sets the value of the username property.
             *
             * @param value allowed object is {@link String }
             *
             */
            public void setUsername(String value) {
                this.username = value;
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
     *         &lt;element name="round" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="game" type="{http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament}gameType" maxOccurs="unbounded"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="number" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
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
        "round"
    })
    public static class Rounds {

        @XmlElement(required = true)
        protected List<TournamentType.Rounds.Round> round;

        /**
         * Gets the value of the round property.
         *
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the round property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRound().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TournamentType.Rounds.Round }
         *
         *
         */
        public List<TournamentType.Rounds.Round> getRound() {
            if (round == null) {
                round = new ArrayList<TournamentType.Rounds.Round>();
            }
            return this.round;
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
         *       &lt;sequence>
         *         &lt;element name="game" type="{http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament}gameType" maxOccurs="unbounded"/>
         *       &lt;/sequence>
         *       &lt;attribute name="number" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "game"
        })
        public static class Round {

            @XmlElement(required = true)
            protected List<GameType> game;
            @XmlAttribute(name = "number", required = true)
            protected int number;

            /**
             * Gets the value of the game property.
             *
             * <p>
             * This accessor method returns a reference to the live list, not a
             * snapshot. Therefore any modification you make to the returned
             * list will be present inside the JAXB object. This is why there is
             * not a
             * <CODE>set</CODE> method for the game property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getGame().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link GameType }
             *
             *
             */
            public List<GameType> getGame() {
                if (game == null) {
                    game = new ArrayList<GameType>();
                }
                return this.game;
            }

            /**
             * Gets the value of the number property.
             *
             */
            public int getNumber() {
                return number;
            }

            /**
             * Sets the value of the number property.
             *
             */
            public void setNumber(int value) {
                this.number = value;
            }
        }
    }
}
