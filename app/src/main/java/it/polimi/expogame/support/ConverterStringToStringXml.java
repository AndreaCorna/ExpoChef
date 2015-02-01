package it.polimi.expogame.support;

import android.content.Context;

import it.polimi.expogame.R;

/**
 * Created by andrea on 01/02/15.
 */
public class ConverterStringToStringXml {

    public static String getStringFromXml(Context context, String string){
        String toLoad = string.replaceAll(" ", "_").toLowerCase();
        int id = context.getResources().getIdentifier(toLoad,"string",context.getPackageName());
        if(id == 0){
            return context.getString(R.string.not_found);
        }
        return   context.getResources().getString(id);

    }
}
