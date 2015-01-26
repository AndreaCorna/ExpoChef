package it.polimi.expogame.support;

import android.widget.TextView;

/**
 * Object use as tag in image adapter draggable in order to pass information
 * about the ingredient related to a single view in the grid
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
