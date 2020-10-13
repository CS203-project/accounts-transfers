package com.ryverbank.accounts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;

/*
{
  "id": 1,
  "nric": "S9123456D",
  "phone": "+6562838283",
  "address": "Address",
  "username": "good_user_5",
  "password": "$2a$12$7tL.TUwiXK/57.KYPcJxWO3tkBPzAEhN.7GFWdU0DH9eyzKArnexG",
  "authorities": "ROLE_USER",
  "active": true,
  "full_name": "Full Name"
}
*/

@Entity
public class Account {
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="account_id")
    private int id;

    // private User user;
    // private int customer_id = user.getID();

    // @ManyToOne
    // @JoinColumn(name="customer_id")
    private int customer_id;

    private double balance;
    private double available_balance;

    // @ManyToMany
    private Transfer[] transactions;

    public Account() {}

    public String toString() {
        return String.format(
            "Account Details: \n Customer ID: %d \n Account ID: %d \n Balance: %f \n Available Balance: %f\n", this.customer_id, this.id, this.balance, this.available_balance
        );
    }

    // GETTERS
    public int getID() { return this.id; }
    public int getCustomer_id() { return this.customer_id; }
    public double getBalance() { return this.balance; }
    public double getAvailable_balance() { return this.available_balance; }
    public Transfer[] getTransfers() { return this.transactions; }

    // SETTERS
    public void updateBalance(double addBalance) { 
        this.balance += addBalance;
        this.available_balance += addBalance;
    }
    
    public void setCustomerID(int customer_id) {
        this.customer_id = customer_id;
    }
}