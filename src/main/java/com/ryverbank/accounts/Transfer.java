package com.ryverbank.accounts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;

public class Transfer {
    // ManyToMany with Accounts
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="transfer_id")
    private int id;

    @Column(name="sender_account_id")
    private int from;

    @Column(name="receiver_account_id")
    private int to;

    private double amount;

    public Transfer() {}

    public String toString() {
        return String.format(
            "Transfer Details: Transfer ID: %d \n Amount: %f \n Sender: %d \n Receiver: %d\n", this.id, this.amount, this.from, this.to
        );
    }

    // GETTERS
    public int getTransferID() { return this.id; }
    public double getTransferAmount() { return this.amount; }
    public int getSenderID() { return this.from; }
    public int getReceiverID() { return this.to; }
}