package it.polimi.expogame.support;

/**
 * Created by degrigis on 10/01/15.
 */
public class Mascotte {



    private String name;
    private Integer longi;
    private Integer lat;

    public Mascotte() {
    }

    public Mascotte(String name, Integer longi, Integer lat) {
        this.name = name;
        this.longi = longi;
        this.lat = lat;
    }

    public Integer getLongi() {
        return longi;
    }

    public Integer getLat() {
        return lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(Integer lat) {
        this.lat = lat;
    }

    public void setLongi(Integer longi) {
        this.longi = longi;
    }
}
