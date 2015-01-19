package it.polimi.expogame.support;

/**
 * Created by Lorenzo on 22/12/14.
 */
public class Ingredient {

    private String name;
    private String imageUrl;
    private String category;
    private boolean unlocked;

    public Ingredient(){}

    public Ingredient(String name, String imageUrl, String category, boolean unlocked){
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.unlocked = unlocked;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isUnlocked() {
        return unlocked;
    }
}
