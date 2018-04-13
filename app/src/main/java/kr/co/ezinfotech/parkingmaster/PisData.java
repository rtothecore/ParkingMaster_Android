package kr.co.ezinfotech.parkingmaster;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkim on 2018-04-04.
 */

public class PisData implements Parcelable {

    Location loc;       // 위,경도
    int emvhNum;       // 긴급차량 잔여 주차구역 개수
    int etcNum;        // 기타 잔여 주차구역 개수
    int gnrlNum;       // 일반 잔여 주차구역 개수
    int hndcNum;       // 장애인 잔여 주차구역 개수
    int hvvhNum;       // 대형 잔여 주차구역 개수
    String addr;        // 주차장 위치 주소
    int lgvhNum;       // 경차 잔여 주차구역 개수
    int wholNum;       // 전체 주차면수
    int wmonNum;       // 여성 잔여 주차구역 개수

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.loc, flags);
        dest.writeInt(this.emvhNum);
        dest.writeInt(this.etcNum);
        dest.writeInt(this.gnrlNum);
        dest.writeInt(this.hndcNum);
        dest.writeInt(this.hvvhNum);
        dest.writeString(this.addr);
        dest.writeInt(this.lgvhNum);
        dest.writeInt(this.wholNum);
        dest.writeInt(this.wmonNum);
    }

    public PisData() {
    }

    protected PisData(Parcel in) {
        this.loc = in.readParcelable(Location.class.getClassLoader());
        this.emvhNum = in.readInt();
        this.etcNum = in.readInt();
        this.gnrlNum = in.readInt();
        this.hndcNum = in.readInt();
        this.hvvhNum = in.readInt();
        this.addr = in.readString();
        this.lgvhNum = in.readInt();
        this.wholNum = in.readInt();
        this.wmonNum = in.readInt();
    }

    public static final Parcelable.Creator<PisData> CREATOR = new Parcelable.Creator<PisData>() {
        @Override
        public PisData createFromParcel(Parcel source) {
            return new PisData(source);
        }

        @Override
        public PisData[] newArray(int size) {
            return new PisData[size];
        }
    };
}
