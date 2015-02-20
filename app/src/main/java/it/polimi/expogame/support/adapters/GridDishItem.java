package it.polimi.expogame.support.adapters;

import android.content.Context;

import it.polimi.expogame.support.converters.ConverterImageNameToDrawableId;

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
        this.imageId = ConverterImageNameToDrawableId.convertImageNameToDrawable(context, imageName);
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


}
