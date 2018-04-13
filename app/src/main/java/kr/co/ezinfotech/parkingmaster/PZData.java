package kr.co.ezinfotech.parkingmaster;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkim on 2018-04-04.
 */

public class PZData implements Parcelable {

    Location loc;       // 위,경도
    int no;
    String name;
    String addr;
    String tel;
    String totalP;
    String opDate;
    String wOpStart;
    String wOpEnd;
    String sOpStart;
    String sOpEnd;
    String hOpStart;
    String hOpEnd;
    String feeInfo;
    String baseTime;
    String baseFee;
    String addTermTime;
    String addTermFee;
    String remarks;

    public PZData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.loc, flags);
        dest.writeInt(this.no);
        dest.writeString(this.name);
        dest.writeString(this.addr);
        dest.writeString(this.tel);
        dest.writeString(this.totalP);
        dest.writeString(this.opDate);
        dest.writeString(this.wOpStart);
        dest.writeString(this.wOpEnd);
        dest.writeString(this.sOpStart);
        dest.writeString(this.sOpEnd);
        dest.writeString(this.hOpStart);
        dest.writeString(this.hOpEnd);
        dest.writeString(this.feeInfo);
        dest.writeString(this.baseTime);
        dest.writeString(this.baseFee);
        dest.writeString(this.addTermTime);
        dest.writeString(this.addTermFee);
        dest.writeString(this.remarks);
    }

    protected PZData(Parcel in) {
        this.loc = in.readParcelable(Location.class.getClassLoader());
        this.no = in.readInt();
        this.name = in.readString();
        this.addr = in.readString();
        this.tel = in.readString();
        this.totalP = in.readString();
        this.opDate = in.readString();
        this.wOpStart = in.readString();
        this.wOpEnd = in.readString();
        this.sOpStart = in.readString();
        this.sOpEnd = in.readString();
        this.hOpStart = in.readString();
        this.hOpEnd = in.readString();
        this.feeInfo = in.readString();
        this.baseTime = in.readString();
        this.baseFee = in.readString();
        this.addTermTime = in.readString();
        this.addTermFee = in.readString();
        this.remarks = in.readString();
    }

    public static final Parcelable.Creator<PZData> CREATOR = new Parcelable.Creator<PZData>() {
        @Override
        public PZData createFromParcel(Parcel source) {
            return new PZData(source);
        }

        @Override
        public PZData[] newArray(int size) {
            return new PZData[size];
        }
    };
}
