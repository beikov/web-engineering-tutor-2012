package tuwien.big.formel0.entities;

import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 *
 * Contains all current registered players
 */
@ManagedBean(name = "rpp")
@ApplicationScoped
public class RegisteredPlayerPool {

    @ManagedProperty("#{dataService}")
    private DataService dataService;

    public boolean addPlayer(Player p) {
        try {
            dataService.persist(p);
        } catch(RuntimeException ex) {
            return false;
        }
        
        return true;
    }

    public Player getRegisteredPlayer(String username, String password) {
        ParameterBinding[] params = new ParameterBinding[2];
        params[0] = new ParameterBinding("username", username);
        params[1] = new ParameterBinding("password", password);

        return dataService.executeScalarNamedQuery(Player.class,
                "Player.getPlayerWithPassword",
                params);
    }

    /**
     * @return the players
     */
    public List<Player> getRegplayers() {
        return dataService.findAll(Player.class);
    }

    public DataService getDataService() {
        return dataService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
}
