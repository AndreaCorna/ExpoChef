package it.polimi.expogame.database.objects;

import android.content.Context;

import it.polimi.expogame.support.converters.ConverterStringToStringXml;

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
    private String hashIngredients;
    private String curiosity;
    private  Integer difficulty;
    private Context context;

    public Dish(){}

    public Dish(Context context,long id, String name, String nationality, String imageUrl, String description, String zone, boolean created, String hashIngredients,String curiosity,Integer difficulty){
        this.context = context;
        this.id = id;
        this.name = name;
        this.nationality = nationality;
        this.imageUrl = imageUrl;
        this.description = ConverterStringToStringXml.getStringFromXml(context, name.replaceAll(" ", "_").toLowerCase() + "_descr");
        this.zone = zone;
        this.created = created;
        this.hashIngredients = hashIngredients;
        this.curiosity = ConverterStringToStringXml.getStringFromXml(context,name.replaceAll(" ", "_").toLowerCase()+"_curiosity");
        this.difficulty = difficulty;

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

    public String getHashIngredients() {
        return hashIngredients;
    }

    public String getCuriosity() {
        return curiosity;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public String getTranslationNationality(){
        return ConverterStringToStringXml.getStringFromXml(context.getApplicationContext(), nationality);
    }


}
