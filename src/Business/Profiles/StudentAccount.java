/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.Profiles;

/**
 *
 * @author kal bugrara
 */
import Business.Finance.Payment;
import java.util.ArrayList;
import java.util.Date;

public class StudentAccount {

    private double balance;
    private ArrayList<Payment> paymentHistory;

    public StudentAccount() {
        this.balance = 0.0;
        this.paymentHistory = new ArrayList<>();
    }

    public void billTuition(double amount) {
        balance += amount;
        paymentHistory.add(new Payment(amount, "Tuition", "Billed", new Date()));
    }

    public void makePayment(double amount) {
        balance -= amount;
        paymentHistory.add(new Payment(amount, "Payment", "Paid", new Date()));
    }

    public void refund(double amount) {
        balance -= amount;
        paymentHistory.add(new Payment(amount, "Refund", "Refunded", new Date()));
    }

    public double getOutstandingBalance() {
        return balance;
    }

    public ArrayList<Payment> getPaymentHistory() {
        return paymentHistory;
    }

    @Override
    public String toString() {
        return "Balance: $" + balance;
    }

}
