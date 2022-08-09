package com.mycompany.spiritus.metier.model;

import javax.persistence.Entity;

@Entity
public class Spirite extends Medium {
    
    private String support;

    public Spirite() {
    }

    public Spirite(String support, String denomination, String presentation, Employee.Gender gender) {
        super(denomination, presentation, gender);
        this.support = support;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    @Override
    public String toString() {
        return "Spirite{" +
                "id='" + this.getId() + '\'' +
                "denomination='" + this.getDenomination() + '\'' +
                ", presentation='" + this.getPresentation() + '\'' +
                ", support='" + support + '\'' +
                '}';
    }
}
