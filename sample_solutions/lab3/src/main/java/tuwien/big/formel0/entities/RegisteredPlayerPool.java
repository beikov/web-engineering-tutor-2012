package tuwien.big.formel0.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;

/**
 *
 * Contains all current registered players
 */
@ManagedBean(name = "rpp")
@ApplicationScoped
public class RegisteredPlayerPool {

    ConcurrentMap<String, Player> regplayers = null;

    /**
     * Creates a new instance of RegisteredPlayerPool
     */
    public RegisteredPlayerPool() {
        regplayers = new ConcurrentHashMap<String, Player>();

        //Add test player
        Player tp = new Player();
        tp.setName("t");
        tp.setPassword("t");
        regplayers.put("t", tp);
    }

    public boolean addPlayer(Player p) {
        return regplayers.putIfAbsent(p.getName(), p) == null;
    }

    public Player getRegisteredPlayer(String username, String password) {
        Player curplayer;

        if ((curplayer = regplayers.get(username)) != null) {
            if (curplayer.getPassword().equals(password)) {
                return curplayer;
            }
        }

        return null;
    }

    /**
     * @return the players
     */
    public List<Player> getRegplayers() {
        return new ArrayList<Player>(regplayers.values());
    }
}
