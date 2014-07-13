package tuwien.big.formel0.entities;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.NoneScoped;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import tuwien.big.formel0.picasa.RaceDriver;

@ManagedBean(name = "player")
@NoneScoped
@Entity
@NamedQuery(name = "Player.getPlayerWithPassword",
        query = "SELECT p FROM Player p WHERE p.name = :username AND p.password = :password")
public class Player implements BaseEntity {

    private static final long serialVersionUID = 1L;
    private String firstname = null;
    private String lastname = null;
    private String name = null;
    private String password = null;
    private String birthday = null;
    private String sex = null;
    private RaceDriver avatar = null;

    /**
     * Creates a new instance of Player
     */
    public Player() {
    }

    /**
     * @return the name
     */
    @Id
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname the firstname to set
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return the birthday
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * @param birthday the birthday to set
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     * @return the sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * @param sex the sex to set
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @return the avatar
     */
    @ManyToOne
    public RaceDriver getAvatar() {
        return avatar;
    }

    /**
     * @param avatar the {@link RaceDriver} to set
     */
    public void setAvatar(RaceDriver avatar) {
        this.avatar = avatar;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Player)) {
            return false;
        }
        final Player other = (Player) obj;
        if ((name == null) ? (other.getName() != null) : !name.equals(other.getName())) {
            return false;
        }
        return true;
    }
}
