package com.mycompany.spiritus.metier.model;

import javax.persistence.Entity;

@Entity
public class Cartomancien extends Medium {

    public Cartomancien() {
    }

    public Cartomancien(String denomination, String presentation, Employee.Gender gender) {
        super(denomination, presentation, gender);
    }

    @Override
    public String toString() {
        return "Cartomancien{" +
                "id='" + this.getId() + '\'' +
                ", denomination='" + this.getDenomination() + '\'' +
                ", presentation='" + this.getPresentation() + '\'' +
                "}";
    }
}
