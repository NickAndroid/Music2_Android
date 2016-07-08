package app.dev.nick.music.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaTrack implements Parcelable {

    private String title;

    protected MediaTrack(Parcel in) {
        title = in.readString();
    }

    public static final Creator<MediaTrack> CREATOR = new Creator<MediaTrack>() {
        @Override
        public MediaTrack createFromParcel(Parcel in) {
            return new MediaTrack(in);
        }

        @Override
        public MediaTrack[] newArray(int size) {
            return new MediaTrack[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
    }
}
