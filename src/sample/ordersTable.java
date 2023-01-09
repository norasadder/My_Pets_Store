package sample;

import java.sql.Date;
import java.time.LocalDate;

public class ordersTable {
    public Integer OID,totalPrice;
    public String date ;

    public ordersTable(int oid ,int totalPrice , String date ) {
        this.OID = oid;
        this.totalPrice = totalPrice ;
        this.date = new String(date)  ;
    }

    public Integer getOID () {
        return OID ;
    }

    public Integer getTotalPrice () {
        return totalPrice ;
    }

    public String getDate () {
        return date ;
    }

}
