package ch.bbcag.ebai.utils;

import ch.bbcag.ebai.models.Advert;
import ch.bbcag.ebai.models.Bid;
import ch.bbcag.ebai.models.Location;
import ch.bbcag.ebai.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestDataUtils {

    public static Advert getTestAdvert() {
        return getTestAdverts().get(0);
    }

    public static List<User> getTestUsers() {
        List<User> users = new ArrayList<User>();

        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setName("User " + i);
            Advert advert = new Advert();
            advert.setName("Advert " + i);
            Advert advert2 = new Advert();
            advert2.setName("Advert " + i);
            Set<Advert> adverts = new HashSet<>();
            adverts.add(advert);
            adverts.add(advert2);
            user.setAdverts(adverts);
            Location location = new Location();
            location.setName("Location " + i);
            location.setPlz(i);
            user.setLocation(location);
            Bid bid = new Bid();
            bid.setValue(i);
            bid.setAdvert(advert);
        }

        return users;
    }

    public static List<Location> getTestLocations() {
        List<Location> locations = new ArrayList<Location>();

        for (int i = 0; i < 5; i++) {
            Location location = new Location();
            location.setName("Location " + i);
            location.setPlz(i);
        }

        return locations;
    }

    public static List<Advert> getTestAdverts() {
        List<Advert> adverts = new ArrayList<Advert>();

        for (int i = 0; i < 5; i++) {
            Advert advert = new Advert();
            advert.setId(i);
            advert.setName("Advert " + i);
            User user = new User();
            user.setName("User " + i);
            advert.setUser(user);
            adverts.add(advert);
        }

        return adverts;
    }

    public static List<Bid> getTestBids() {
        List<Bid> bids = new ArrayList<Bid>();

        for (int i = 0; i < 5; i++) {
            Bid bid = new Bid();
            bid.setValue(i);
            User user = new User();
            user.setName("User " + i);
            bid.setUser(user);
            Advert advert = new Advert();
            advert.setName("Advert " + i);
            bid.setAdvert(advert);
            bids.add(bid);
        }

        return bids;
    }
}
