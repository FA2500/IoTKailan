package my.afc.iotBRSpinach;

public class PicoData {
    String humidity1;
    String humidity2;
    String moisture;
    String temperature1;
    String temperature2;
    String water1;
    String water2;

    String waterlevel;


    public PicoData()
    {

    }

    public PicoData(String humidity1, String humidity2, String moisture, String temperature1, String temperature2, String water1, String water2,String waterlevel) {
        this.humidity1 = humidity1;
        this.humidity2 = humidity2;
        this.moisture = moisture;
        this.temperature1 = temperature1;
        this.temperature2 = temperature2;
        this.water1 = water1;
        this.water2 = water2;
        this.waterlevel = waterlevel;
    }

    public String getHumidity1() {
        return humidity1;
    }

    public void setHumidity1(String humidity1) {
        this.humidity1 = humidity1;
    }

    public String getHumidity2() {
        return humidity2;
    }

    public void setHumidity2(String humidity2) {
        this.humidity2 = humidity2;
    }

    public String getMoisture() {
        return moisture;
    }

    public void setMoisture(String moisture) {
        this.moisture = moisture;
    }

    public String getTemperature1() {
        return temperature1;
    }

    public void setTemperature1(String temperature1) {
        this.temperature1 = temperature1;
    }

    public String getTemperature2() {
        return temperature2;
    }

    public void setTemperature2(String temperature2) {
        this.temperature2 = temperature2;
    }

    public String getWater1() {
        return water1;
    }

    public void setWater1(String water1) {
        this.water1 = water1;
    }

    public String getWater2() {
        return water2;
    }

    public void setWater2(String water2) {
        this.water2 = water2;
    }

    public String getWaterlevel() {
        return waterlevel;
    }

    public void setWaterlevel(String waterlevel) {
        this.waterlevel = waterlevel;
    }

}
