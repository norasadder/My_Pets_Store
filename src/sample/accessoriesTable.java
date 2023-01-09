package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class accessoriesTable {

    public Integer PID;
    public String accessoriesType;
    public Integer price ;
    public accessoriesTable(Integer pid,String accessoriesType , Integer price ) {
        this.PID = pid;
        this.accessoriesType = accessoriesType  ;
        this.price =  price ;
    }
    public String getAccessoriesType () {
        return accessoriesType ;
    }

    public Integer getPrice () {
        return price ;
    }

    public Integer getPID () {
        return PID ;
    }

}
