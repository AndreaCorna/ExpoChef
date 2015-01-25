package it.polimi.expogame.support;

import android.content.Context;

import it.polimi.expogame.R;

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
        this.idDrawable = ConverterImageNameToDrawableId.convertImageNameToDrawable(context,imageUlr);
    }

    public String getName() {
        return name;
    }

    public int getIdDrawable() {
        return idDrawable;
    }


}
