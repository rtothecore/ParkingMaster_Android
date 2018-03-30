package kr.co.ezinfotech.parkingmaster;

/**
 * Created by hkim on 2018-03-27.
 * http://www.zoftino.com/google-maps-android-custom-info-window-example
 */

public class InfoWindowData {
    private String pkLocName;
    private String pkTotal;
    private String pkUse;
    private String pkAvail;
    private String wtStatus;
    private String wtTemp;
    private String wtHum;

    public String getPkLocName() {
        return pkLocName;
    }

    public void setPkLocName(String pkLocName) {
        this.pkLocName = pkLocName;
    }

    public String getPkTotal() {
        return pkTotal;
    }

    public void setPkTotal(String pkTotal) {
        this.pkTotal = pkTotal;
    }

    public String getPkUse() {
        return pkUse;
    }

    public void setPkUse(String pkUse) {
        this.pkUse = pkUse;
    }

    public String getPkAvail() {
        return pkAvail;
    }

    public void setPkAvail(String pkAvail) {
        this.pkAvail = pkAvail;
    }

    public String getWtStatus() {
        return wtStatus;
    }

    public void setWtStatus(String wtStatus) {
        this.wtStatus = wtStatus;
    }

    public String getWtTemp() {
        return wtTemp;
    }

    public void setWtTemp(String wtTemp) {
        this.wtTemp = wtTemp;
    }

    public String getWtHum() {
        return wtHum;
    }

    public void setWtHum(String wtHum) {
        this.wtHum = wtHum;
    }
}
