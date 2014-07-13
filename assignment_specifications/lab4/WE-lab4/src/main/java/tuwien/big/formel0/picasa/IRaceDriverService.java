package tuwien.big.formel0.picasa;

import java.io.IOException;
import java.util.List;

import com.google.gdata.util.ServiceException;

/**
 * Interface for a connector used to retrieve race drivers
 *
 * @author pl
 *
 */
public interface IRaceDriverService {

    public List<RaceDriver> getRaceDrivers() throws IOException, ServiceException;
}
