package sample;


public class orderProductTable {

    public Integer PID,totalPrice,quantity;

    public orderProductTable(int pid ,int totalPrice , int quantity ) {
        this.PID = pid;
        this.totalPrice = totalPrice ;
        this.quantity = quantity  ;
    }

    public Integer getPID () {
        return PID ;
    }

    public Integer getTotalPrice () {
        return totalPrice ;
    }

    public Integer getQuantity () {
        return quantity;
    }

}
