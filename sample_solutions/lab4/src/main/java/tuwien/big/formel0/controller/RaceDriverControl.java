package tuwien.big.formel0.controller;

import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import tuwien.big.formel0.entities.DataService;
import tuwien.big.formel0.picasa.IRaceDriverService;
import tuwien.big.formel0.picasa.RaceDriver;
import tuwien.big.formel0.picasa.RaceDriverService;

/**
 *
 * @author petra
 */
@ManagedBean(name = "rdc")
@ApplicationScoped
public class RaceDriverControl {

    private static final Logger logger = Logger.getLogger(DataService.class.getName());
    private List<RaceDriver> drivers;
    @ManagedProperty("#{dataService}")
    private DataService dataService;

    public RaceDriverControl() {
    }

    @PostConstruct
    private void initialize() {
        logger.log(Level.INFO, "{0} initialized.", this.getClass().getName());

        IRaceDriverService raceDriverService = new RaceDriverService();
        drivers = new LinkedList<RaceDriver>();

        try {
            drivers = raceDriverService.getRaceDrivers();
            for (RaceDriver driver : drivers) {
                dataService.persist(driver);
            }
        } catch (IOException ex) {
            Logger.getLogger(RaceDriverControl.class.getName()).log(Level.SEVERE, "Unable to connect to service", ex);
        } catch (ServiceException ex) {
            Logger.getLogger(RaceDriverControl.class.getName()).log(Level.SEVERE, "Unable to connect to service", ex);
        }
    }

    /**
     * @return the drivers
     */
    public List<RaceDriver> getDrivers() {
        return drivers;
    }

    /**
     * @param drivers the {@link RaceDriver} to set
     */
    public void setDrivers(List<RaceDriver> drivers) {
        this.drivers = drivers;
    }

    public DataService getDataService() {
        return dataService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
}
