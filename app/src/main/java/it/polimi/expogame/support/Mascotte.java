package it.polimi.expogame.support;

/**
 * Created by degrigis on 10/01/15.
 */
public class Mascotte {

    private String name;
    private String longi;
    private String lat;
    private int captured;

    public Mascotte() {
    }

    public Mascotte(String name, String lat, String longi) {
        this.name = name;
        this.longi = longi;
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public String getLat() {
        return lat;
    }

    public String getName() {
        return name;
    }

    public int getCaptured(){return captured;}


    public void setName(String name) {
        this.name = name;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public void setCaptured(int value){ this.captured = value;}
}
