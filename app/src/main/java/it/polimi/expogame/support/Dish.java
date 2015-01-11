package it.polimi.expogame.support;

/**
 * Created by Lorenzo on 22/12/14.
 */
public class Dish {

    private long id;
    private String name;
    private String nationality;
    private String imageUrl;
    private String description;
    private String zone;
    private boolean created;

    public Dish(){}

    public Dish(long id, String name, String nationality, String imageUrl, String description, String zone, boolean created){
        this.id = id;
        this.name = name;
        this.nationality = nationality;
        this.imageUrl = imageUrl;
        this.description = description;
        this.zone = zone;
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNationality() {
        return nationality;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isCreated() {
        return created;
    }

    public String getZone() {
        return zone;
    }

    public String getDescription() {
        return description;
    }
}
