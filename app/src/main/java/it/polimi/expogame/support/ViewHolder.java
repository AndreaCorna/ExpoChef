package it.polimi.expogame.support;

import android.widget.TextView;

/**
 * Created by andrea on 23/01/15.
 */
public  class ViewHolder {

    private  SquareImageView picture;
    private  TextView text;
    private  Ingredient ingredient;

    public SquareImageView getPicture() {
        return picture;
    }

    public void setPicture(SquareImageView picture) {
        this.picture = picture;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }
}
