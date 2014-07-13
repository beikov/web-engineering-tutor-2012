package tuwien.big.formel0.highscore.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for HighScoreRequestType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType name="HighScoreRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://big.tuwien.ac.at/we/highscore/data}UserKey"/>
 *         &lt;element ref="{http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament}tournament"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HighScoreRequestType", namespace = "http://big.tuwien.ac.at/we/highscore/data", propOrder = {
    "userKey",
    "tournament"
})
public class HighScoreRequestType {

    @XmlElement(name = "UserKey", required = true)
    protected String userKey;
    @XmlElement(namespace = "http://www.dbai.tuwien.ac.at/education/ssd/SS13/uebung/Tournament", required = true)
    protected TournamentType tournament;

    /**
     * Gets the value of the userKey property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getUserKey() {
        return userKey;
    }

    /**
     * Sets the value of the userKey property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setUserKey(String value) {
        this.userKey = value;
    }

    /**
     * Gets the value of the tournament property.
     *
     * @return possible object is {@link TournamentType }
     *
     */
    public TournamentType getTournament() {
        return tournament;
    }

    /**
     * Sets the value of the tournament property.
     *
     * @param value allowed object is {@link TournamentType }
     *
     */
    public void setTournament(TournamentType value) {
        this.tournament = value;
    }
}
