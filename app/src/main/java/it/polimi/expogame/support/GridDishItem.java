package it.polimi.expogame.support;

import android.content.Context;

import it.polimi.expogame.R;

/**
 * Created by andrea on 25/01/15.
 */
public class GridDishItem {

    private Context context;
    private String name;
    private int imageId;
    private long id;
    private boolean isCreated;

    public GridDishItem(Context context, long id, String name, String imageName, boolean isCreated) {
        this.context = context.getApplicationContext();
        this.name = name;
        this.imageId = convertImageNameToDrawable(imageName);
        this.isCreated = isCreated;
        this.id = id ;
    }

    public Context getContext() {
        return context;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public int getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

    public long getId(){return id;}

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
