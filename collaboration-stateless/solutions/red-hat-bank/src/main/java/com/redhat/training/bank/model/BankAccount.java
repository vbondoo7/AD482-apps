package com.redhat.training.bank.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;

@Entity
@Cacheable
public class BankAccount extends PanacheEntity {

    public Long balance;

    public String profile;

    public BankAccount() {
    }

    public BankAccount(Long balance, String profile) {
        this.balance = balance;
        this.profile = profile;
    }
}
