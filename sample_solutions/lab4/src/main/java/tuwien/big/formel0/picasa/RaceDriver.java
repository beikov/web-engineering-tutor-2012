package tuwien.big.formel0.picasa;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.NoneScoped;
import javax.persistence.Entity;
import javax.persistence.Id;
import tuwien.big.formel0.entities.BaseEntity;
import tuwien.big.formel0.entities.Player;

@ManagedBean(name = "avatar")
@NoneScoped
@Entity
public class RaceDriver implements BaseEntity {

    private String name;
    private String url;
    private String wikiUrl;

    public RaceDriver() {
    }

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    @Override
    public String toString() {
        return "RaceDriver [name=" + name + ", url=" + url + ", wikiUrl="
                + wikiUrl + "]";
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
        final RaceDriver other = (RaceDriver) obj;
        if ((name == null) ? (other.getName() != null) : !name.equals(other.getName())) {
            return false;
        }
        return true;
    }
}
