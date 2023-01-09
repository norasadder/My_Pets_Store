package sample;

public class orderDetailsTable {
    public Integer OID,totalPrice,p,o,d;
    public String date ;

    public orderDetailsTable(int oid ,int totalPrice , String date ,int p,int o,int d) {
        this.OID = oid;
        this.totalPrice = totalPrice ;
        this.date = new String(date)  ;
        this.p=p;
        this.o=o;
        this.d=d;
    }

    public Integer getOID () {
        return OID ;
    }

    public Integer getTotalPrice() {
        return totalPrice ;
    }

    public String getDate () {
        return date ;
    }

    public Integer getP () {
        return p;
    }

    public Integer getO () {
        return o;
    }

    public Integer getD () {
        return d;
    }


}
