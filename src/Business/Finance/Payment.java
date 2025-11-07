/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Finance;

import java.util.Date;

/**
 *
 * @author chinm
 */
public class Payment {

    private double amount;
    private Date date;
    private String type;
    private String status;

    public Payment(double amount, String type, String status, Date date) {
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.date = date;
    }

    public Payment(double amount, String type, String status) {
        this(amount, type, status, new Date());
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return type + " - $" + amount + " (" + status + ") on " + date;
    }

}
