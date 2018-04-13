package kr.co.ezinfotech.parkingmaster;

/**
 * Created by hkim on 2018-04-03.
 */

public class ParkingZoneDBCtrct {

    private ParkingZoneDBCtrct() {};

    public static final String TBL_PARKING_ZONE = "PARKING_ZONE";
    public static final String COL_NO = "NO";
    public static final String COL_NAME = "NAME";
    public static final String COL_ADDR = "ADDR";
    public static final String COL_TEL = "TEL";
    public static final String COL_LAT = "LAT";
    public static final String COL_LNG = "LNG";
    public static final String COL_TOTALP = "TOTALP";
    public static final String COL_OPINFO = "OPDATE";
    public static final String COL_WOPSTART = "WOPSTART";
    public static final String COL_WOPEND = "WOPEND";
    public static final String COL_SOPSTART = "SOPSTART";
    public static final String COL_SOPEND = "SOPEND";
    public static final String COL_HOPSTART = "HOPSTART";
    public static final String COL_HOPEND = "HOPEND";
    public static final String COL_FEEINFO = "FEEINFO";
    public static final String COL_BASETIME = "BASETIME";
    public static final String COL_BASEFEE = "BASEFEE";
    public static final String COL_ADDTERMTIME = "ADDTERMTIME";
    public static final String COL_ADDTERMFEE = "ADDTERMFEE";
    public static final String COL_REMARKS = "REMARKS";

    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_PARKING_ZONE + " " +
            "(" +
                COL_NO +            " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
                COL_NAME +          " TEXT"                                   + ", " +
                COL_ADDR +          " TEXT"                                   + ", " +
                COL_TEL +           " TEXT"                                   + ", " +
                COL_LAT +           " TEXT"                                   + ", " +
                COL_LNG +           " TEXT"                                   + ", " +
                COL_TOTALP +        " TEXT"                                   + ", " +
                COL_OPINFO +        " TEXT"                                   + ", " +
                COL_WOPSTART +        " TEXT"                                   + ", " +
                COL_WOPEND +        " TEXT"                                   + ", " +
                COL_SOPSTART +        " TEXT"                                   + ", " +
                COL_SOPEND +        " TEXT"                                   + ", " +
                COL_HOPSTART +        " TEXT"                                   + ", " +
                COL_HOPEND +        " TEXT"                                   + ", " +
                COL_FEEINFO +        " TEXT"                                   + ", " +
                COL_BASETIME +        " TEXT"                                   + ", " +
                COL_BASEFEE +        " TEXT"                                   + ", " +
                COL_ADDTERMTIME +        " TEXT"                                   + ", " +
                COL_ADDTERMFEE +        " TEXT"                                   + ", " +
                COL_REMARKS +        " TEXT"                                   +
            ")";

    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_PARKING_ZONE;

    public static final String SQL_SELECT = "SELECT * FROM " + TBL_PARKING_ZONE;

    public static final String SQL_SELECT_LAT_LNG_WITH_NAME = "SELECT " + COL_LAT + ", " + COL_LNG + " FROM " + TBL_PARKING_ZONE + " WHERE " + COL_NAME + "='";

    public static final String SQL_SELECT_NO_WITH_NAME = "SELECT " + COL_NO + " FROM " + TBL_PARKING_ZONE + " WHERE " + COL_NAME + "='";

    public static final String SQL_SELECT_NO_LAT_LNG_NAME = "SELECT " + COL_NO + ", " + COL_LAT + ", " + COL_LNG  + ", " + COL_NAME + " FROM " + TBL_PARKING_ZONE;

    public static final String SQL_SELECT_WITH_NO = "SELECT " + COL_NO + ", " + COL_NAME + ", " + COL_ADDR + ", " + COL_TEL + ", " + COL_LAT + ", " + COL_LNG + ", " + COL_TOTALP + ", " +
                                                                   COL_OPINFO + ", " + COL_WOPSTART + ", " + COL_WOPEND + ", " + COL_SOPSTART + ", " + COL_SOPEND + ", " + COL_HOPSTART + ", " + COL_HOPEND +
                                                                   ", " + COL_FEEINFO + ", " + COL_BASETIME + ", " + COL_BASEFEE + ", " + COL_ADDTERMTIME + ", " + COL_ADDTERMFEE + ", " + COL_REMARKS +
                                                                   " FROM " + TBL_PARKING_ZONE + " WHERE " + COL_NO + "=";

    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_PARKING_ZONE + " " +
            "(" + COL_NAME + ", " + COL_ADDR + ", " + COL_TEL + ", " + COL_LAT + ", " + COL_LNG + ", " + COL_TOTALP + ", " + COL_OPINFO + ", " +
                  COL_WOPSTART + ", " + COL_WOPEND + ", " + COL_SOPSTART + ", " + COL_SOPEND + ", " + COL_HOPSTART + ", " + COL_HOPEND + ", " + COL_FEEINFO + ", " +
                  COL_BASETIME + ", " + COL_BASEFEE + ", " + COL_ADDTERMTIME + ", " + COL_ADDTERMFEE + ", " + COL_REMARKS +
            ") VALUES ";

    public static final String SQL_DELETE = "DELETE FROM " + TBL_PARKING_ZONE;
}
