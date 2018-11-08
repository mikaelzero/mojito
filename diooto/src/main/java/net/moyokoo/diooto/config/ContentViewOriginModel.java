package net.moyokoo.diooto.config;

import android.os.Parcel;
import android.os.Parcelable;

public class ContentViewOriginModel implements Parcelable {
    public int left;
    public int top;
    public int width;
    public int height;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.left);
        dest.writeInt(this.top);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public ContentViewOriginModel() {
    }

    protected ContentViewOriginModel(Parcel in) {
        this.left = in.readInt();
        this.top = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<ContentViewOriginModel> CREATOR = new Parcelable.Creator<ContentViewOriginModel>() {
        @Override
        public ContentViewOriginModel createFromParcel(Parcel source) {
            return new ContentViewOriginModel(source);
        }

        @Override
        public ContentViewOriginModel[] newArray(int size) {
            return new ContentViewOriginModel[size];
        }
    };
}
