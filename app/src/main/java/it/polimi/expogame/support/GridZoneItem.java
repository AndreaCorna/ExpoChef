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
        this.idDrawable = convertImageNameToDrawable(imageUlr);
    }

    public String getName() {
        return name;
    }

    public int getIdDrawable() {
        return idDrawable;
    }

    //Convert the image String into the int representing the R.id drawable
    private int convertImageNameToDrawable(String imageUrl){

        //Retreive the image of the ingredient and add their id
        //to the mThumbIds array

        int index = imageUrl.indexOf(".");
        String urlImage = null;
        //delete extension of file from name if exist
        if (index > 0)
            urlImage = imageUrl.substring(0, index);
        //get the id
        int id = context.getResources().getIdentifier(urlImage, "drawable", context.getPackageName());
        if (id == 0){
            id = R.drawable.ic_launcher;
        }

        return id;
    }
}
