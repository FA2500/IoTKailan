package my.afc.iotBRSpinach;

public class PicoController {

    public Boolean fan;
    public Boolean servo;
    public Boolean waterpump1;
    public Boolean waterpump2;

    public PicoController()
    {

    }

    public PicoController(Boolean fan,Boolean servo,Boolean waterpump1, Boolean waterpump2)
    {
        this.fan = fan;
        this.servo = servo;
        this.waterpump1 = waterpump1;
        this.waterpump2 = waterpump2;
    }

    public Boolean getFan() {
        return fan;
    }

    public void setFan(Boolean fan) {
        this.fan = fan;
    }

    public Boolean getServo() {
        return servo;
    }

    public void setServo(Boolean servo) {
        this.servo = servo;
    }

    public Boolean getWaterpump1() {
        return waterpump1;
    }

    public void setWaterpump1(Boolean waterpump1) {
        this.waterpump1 = waterpump1;
    }

    public Boolean getWaterpump2() {
        return waterpump2;
    }

    public void setWaterpump2(Boolean waterpump2) {
        this.waterpump2 = waterpump2;
    }


}
