package kr.co.ezinfotech.parkingmaster;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hkim on 2018-04-03.
 */

public class ParkingMasterDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 10;
    public static final String DBFILE_PARKING_MASTER = "parkingmaster.db";
    //public static final String DBFILE_PARKING_MASTER = "mnt/sdcard/parkingmaster.db";

    public ParkingMasterDBHelper(Context context) {
        super(context, "/mnt/sdcard/" + DBFILE_PARKING_MASTER, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ParkingZoneDBCtrct.SQL_CREATE_TBL);
        sqLiteDatabase.execSQL(FavoritesDBCtrct.SQL_CREATE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }

    public void onDownGrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
