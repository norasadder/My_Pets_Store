package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class petsTable {

    public Integer PID;
    public String category , petType , gender ;
    public Integer price ;
    public String birthdate ;
    public petsTable(Integer pid,String category , String petType , Integer price , String birthdate , String gender) {
        this.PID = pid;
        this.category = category ;
        this.petType = petType  ;
        this.price =  price ;
        this.gender = gender  ;
        this.birthdate = birthdate ;
    }

    public String getCategory () {
        return category ;
    }

    public String getPetType () {
        return petType ;
    }

    public Integer getPrice () {
        return price ;
    }

    public String getBirthdate () {
        return birthdate ;
    }

    public String getGender () {
        return gender ;
    }

    public Integer getPID () {
        return PID ;
    }

}
