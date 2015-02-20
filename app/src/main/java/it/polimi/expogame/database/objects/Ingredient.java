package it.polimi.expogame.database.objects;

import android.content.Context;

import it.polimi.expogame.support.converters.ConverterImageNameToDrawableId;
import it.polimi.expogame.support.converters.ConverterStringToStringXml;

/**
 * Created by Lorenzo on 22/12/14.
 */
public class Ingredient {

    private String name;
    private int drawableImage;
    private String category;
    private boolean unlocked;
    private Context mContext;

    public Ingredient(){}

    /*
    Context is needed to be able to convert String represeting the name of the image file to the R.id drawable used in android to get the image
      */
    public Ingredient(Context mContext,String name, String imageUrl, String category, boolean unlocked){
        this.mContext = mContext;
        this.name = name;
        this.drawableImage = ConverterImageNameToDrawableId.convertImageNameToDrawable(mContext, imageUrl);
        this.category = category;
        this.unlocked = unlocked;
    }


    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getDrawableImage() {
        return drawableImage;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public String getNameTranslation(){
        return ConverterStringToStringXml.getStringFromXml(mContext, name.replaceAll(" ", "_").toLowerCase());
    }
}
