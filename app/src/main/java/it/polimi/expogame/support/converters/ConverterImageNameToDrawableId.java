package it.polimi.expogame.support.converters;

import android.content.Context;

import it.polimi.expogame.R;

/**
 * Helper class in order to retrieve id of drawable give the image name
 */
public class ConverterImageNameToDrawableId {

    public static int convertImageNameToDrawable(Context context, String imageUrl){


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
