package tuwien.big.formel0.picasa;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


import com.google.gdata.util.ServiceException;

/**
 * Test access to the race driver image gallery on Picasa
 *
 * @author pl
 *
 */
public class RaceDriverServiceTest {

    @Test
    public void retrieveDriversTest() throws IOException, ServiceException {
        IRaceDriverService proxy = new RaceDriverService();
        List<RaceDriver> drivers = proxy.getRaceDrivers();

        //Expecting seven drivers
        Assert.assertNotNull(drivers);
        Assert.assertEquals(7, drivers.size());

        //Expecting the following drivers
        List<String> expectedDrivers = Arrays.asList("Alain Prost", "Ayrton Senna", "Gerhard Berger", "Michael Schuhmacher", "Mika Haekkinen", "Nigel Mansell", "Niki Lauda");

        for (RaceDriver driver : drivers) {
            System.out.println("Found driver " + driver.getName());
            Assert.assertTrue(expectedDrivers.contains(driver.getName()));
        }
    }
}
