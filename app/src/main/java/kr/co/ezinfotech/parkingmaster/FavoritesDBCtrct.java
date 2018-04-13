package kr.co.ezinfotech.parkingmaster;

/**
 * Created by hkim on 2018-04-03.
 */

public class FavoritesDBCtrct {

    private FavoritesDBCtrct() {};

    public static final String TBL_FAVORITES = "FAVORITES";
    public static final String COL_NO = "NO";

    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_FAVORITES + " " +
            "(" +
                //"FOREIGN KEY(" + COL_NO + ") REFERENCES PARKING_ZONE(NO)" +
                COL_NO + " INTEGER NOT NULL" +
            ")";

    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_FAVORITES;

    public static final String SQL_SELECT = "SELECT * FROM " + TBL_FAVORITES;

    public static final String SQL_SELECT_WITH_NO = "SELECT * FROM " + TBL_FAVORITES + " WHERE " + COL_NO + "=";

    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_FAVORITES + " " +
            "(" + COL_NO + ") VALUES ";

    public static final String SQL_DELETE = "DELETE FROM " + TBL_FAVORITES;

    public static final String SQL_DELETE_WITH_NO = "DELETE FROM " + TBL_FAVORITES + " WHERE " + COL_NO + "=";
}
