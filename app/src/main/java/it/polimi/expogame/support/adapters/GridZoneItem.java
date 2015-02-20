package it.polimi.expogame.support.adapters;

import android.content.Context;

import it.polimi.expogame.support.converters.ConverterImageNameToDrawableId;
import it.polimi.expogame.support.converters.ConverterStringToStringXml;

/**
 * Item for the grid view in the zone list
 */
public class GridZoneItem {

    private Context context;
    private String name;
    private int idDrawable;


    public GridZoneItem(Context mContext, String name, String imageUlr){
        this.context = mContext;
        this.name = name;
        this.idDrawable = ConverterImageNameToDrawableId.convertImageNameToDrawable(context, imageUlr);

    }

    public String getName() {
        return name;
    }

    public int getIdDrawable() {
        return idDrawable;
    }

    public String getTranslation(){
        String translation = ConverterStringToStringXml.getStringFromXml(context, name);
        return Character.toString(translation.charAt(0)).toUpperCase()+translation.substring(1);
    }


}
