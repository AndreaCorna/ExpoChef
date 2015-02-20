package it.polimi.expogame.support;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Lorenzo on 20/02/15.
 */
public class Hint implements Parcelable{
        private String name;
        private int drawableImage;
        private boolean hintGiven;

        public Hint(Context context, String name,String imageUrl, boolean hintGiven){
            this.name = name;
            this.hintGiven = hintGiven;
            this.drawableImage = ConverterImageNameToDrawableId.convertImageNameToDrawable(context,imageUrl);
        }

        // Parcelling part
        public Hint(Parcel in){

            this.name = in.readString();
            this.drawableImage = in.readInt();
            this.hintGiven = in.readByte()==1? true : false;
        }


        public String getName(){
            return this.name;
        }

        public boolean alreadySuggested(){
            return this.hintGiven;
        }

        public int getDrawableImage(){
            return this.drawableImage;
        }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(drawableImage);
        dest.writeString(name);
        dest.writeByte((byte) (hintGiven ? 1 : 0));

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Hint createFromParcel(Parcel in) {
            return new Hint(in);
        }

        public Hint[] newArray(int size) {
            return new Hint[size];
        }
    };

}
