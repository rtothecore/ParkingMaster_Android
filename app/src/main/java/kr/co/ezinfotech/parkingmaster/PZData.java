package kr.co.ezinfotech.parkingmaster;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkim on 2018-04-04.
 */

public class PZData implements Parcelable {

    Location loc;       // 위,경도
    String no;
    String name;
    String addr;
    String tel;
    String totalP;
    String opDate;

    PZTermData w_op;
    PZTermData s_op;
    PZTermData h_op;

    String feeInfo;

    PZTFData park_base;
    PZTFData add_term;
    PZTFData one_day_park;

    String remarks;
    String dataDate;

    public PZData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.loc, flags);
        dest.writeString(this.no);
        dest.writeString(this.name);
        dest.writeString(this.addr);
        dest.writeString(this.tel);
        dest.writeString(this.totalP);
        dest.writeString(this.opDate);

        dest.writeString(this.w_op.start_date);
        dest.writeString(this.w_op.end_date);
        dest.writeString(this.s_op.start_date);
        dest.writeString(this.s_op.end_date);
        dest.writeString(this.h_op.start_date);
        dest.writeString(this.h_op.end_date);

        dest.writeString(this.feeInfo);

        dest.writeString(this.park_base.time);
        dest.writeString(this.park_base.fee);
        dest.writeString(this.add_term.time);
        dest.writeString(this.add_term.fee);
//        dest.writeString(this.one_day_park.time);
//        dest.writeString(this.one_day_park.fee);

        dest.writeString(this.remarks);
        dest.writeString(this.dataDate);
    }

    protected PZData(Parcel in) {
        this.loc = in.readParcelable(Location.class.getClassLoader());
        this.no = in.readString();
        this.name = in.readString();
        this.addr = in.readString();
        this.tel = in.readString();
        this.totalP = in.readString();
        this.opDate = in.readString();

        this.w_op = new PZTermData();
        this.w_op.start_date = in.readString();
        this.w_op.end_date = in.readString();
        this.s_op = new PZTermData();
        this.s_op.start_date = in.readString();
        this.s_op.start_date = in.readString();
        this.h_op = new PZTermData();
        this.h_op.start_date = in.readString();
        this.h_op.start_date = in.readString();

        this.feeInfo = in.readString();

        this.park_base = new PZTFData();
        this.park_base.time = in.readString();
        this.park_base.fee = in.readString();
        this.add_term = new PZTFData();
        this.add_term.time = in.readString();
        this.add_term.fee = in.readString();
//        this.one_day_park.time = in.readString();
//        this.one_day_park.fee = in.readString();

        this.remarks = in.readString();
        this.dataDate = in.readString();
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
