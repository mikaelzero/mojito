package net.mikaelzero.diooto.config;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class DiootoConfig implements Parcelable {

    private String[] imageUrls;
    private boolean isFullScreen = false;
    private List<ContentViewOriginModel> contentViewOriginModels;
    private int position;
    private boolean immersive;
    private int headerSize;
    private int indicatorVisibility;

    public int getIndicatorVisibility() {
        return indicatorVisibility;
    }

    public void setIndicatorVisibility(int indicatorVisibility) {
        this.indicatorVisibility = indicatorVisibility;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }

    public boolean isImmersive() {
        return immersive;
    }

    public void setImmersive(boolean immersive) {
        this.immersive = immersive;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public List<ContentViewOriginModel> getContentViewOriginModels() {
        return contentViewOriginModels;
    }

    public void setContentViewOriginModels(List<ContentViewOriginModel> contentViewOriginModels) {
        this.contentViewOriginModels = contentViewOriginModels;
    }

    public DiootoConfig() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.imageUrls);
        dest.writeByte(this.isFullScreen ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.contentViewOriginModels);
        dest.writeInt(this.position);
        dest.writeByte(this.immersive ? (byte) 1 : (byte) 0);
        dest.writeInt(this.headerSize);
        dest.writeInt(this.indicatorVisibility);
    }

    protected DiootoConfig(Parcel in) {
        this.imageUrls = in.createStringArray();
        this.isFullScreen = in.readByte() != 0;
        this.contentViewOriginModels = in.createTypedArrayList(ContentViewOriginModel.CREATOR);
        this.position = in.readInt();
        this.immersive = in.readByte() != 0;
        this.headerSize = in.readInt();
        this.indicatorVisibility = in.readInt();
    }

    public static final Creator<DiootoConfig> CREATOR = new Creator<DiootoConfig>() {
        @Override
        public DiootoConfig createFromParcel(Parcel source) {
            return new DiootoConfig(source);
        }

        @Override
        public DiootoConfig[] newArray(int size) {
            return new DiootoConfig[size];
        }
    };
}
