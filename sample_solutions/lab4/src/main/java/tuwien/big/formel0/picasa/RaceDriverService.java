package tuwien.big.formel0.picasa;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.ServiceException;

/**
 * Class for accessing the list of race drivers from Picasa
 *
 * @author pl
 *
 */
public class RaceDriverService implements IRaceDriverService {

    private static String albumUrl = "https://picasaweb.google.com/data/feed/api/user/107302466601293793664";
    private static final String ALBUMID = "Rennfahrer";
    private PicasawebService myService;
    private static List<RaceDriver> drivers;

    /**
     * Get the list of drivers
     *
     * @throws ServiceException
     * @throws IOException
     */
    @Override
    public List<RaceDriver> getRaceDrivers() throws IOException, ServiceException {
        if (drivers == null) {
            drivers = new LinkedList<RaceDriver>();

            // Setup the service
            myService = new PicasawebService("myService");
            URL feedUrl = new URL(albumUrl);

            // Get a list of the Albums and retrieve the "Rennfahrer" album
            UserFeed myUserFeed = myService.getFeed(feedUrl, UserFeed.class);
            AlbumEntry rennfahrerAlbum = null;

            for (AlbumEntry myAlbum : myUserFeed.getAlbumEntries()) {
                if (ALBUMID.equals(myAlbum.getTitle().getPlainText())) {
                    rennfahrerAlbum = myAlbum;
                    break;
                }
            }

            if (rennfahrerAlbum != null) {
                // Get the photos from the album
                AlbumFeed feed = myService.getFeed(new URL(rennfahrerAlbum
                        .getFeedLink().getHref()), AlbumFeed.class);

                for (PhotoEntry photo : feed.getPhotoEntries()) {
                    //Is it a driver's photo?
                    if (isDriver(photo)) {

                        RaceDriver driver = new RaceDriver();
                        driver.setUrl(photo.getMediaThumbnails().get(1).getUrl());
                        driver.setName(photo.getDescription().getPlainText());
                        driver.setWikiUrl(getWikiUrl(photo));

                        drivers.add(driver);

                    }
                }
            }
        }

        return drivers;
    }

    /**
     * Determines if the given photo is from a formula one driver
     *
     * @param entry
     * @return
     */
    private boolean isDriver(PhotoEntry photo) {
        if (photo.getMediaKeywords() != null) {
            for (String tag : photo.getMediaKeywords().getKeywords()) {
                if (tag.equals("Driver")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the Wikipedia URL of a driver
     *
     * @param photo
     * @return
     */
    private String getWikiUrl(PhotoEntry photo) {
        if (photo.getMediaKeywords() != null) {
            for (String tag : photo.getMediaKeywords().getKeywords()) {
                if (tag.startsWith("wiki:")) {
                    return "http://" + tag.replaceAll("wiki:", "");
                }
            }
        }

        return "";
    }
}
