package com.mycompany.spiritus.metier.model;

import javax.persistence.Entity;

@Entity
public class Astrologue extends Medium {
    
    private String formation;
    private String promotion;

    public Astrologue() {
    }

    public Astrologue(String formation, String promotion, String denomination, String presentation, Employee.Gender gender) {
        super(denomination, presentation, gender);
        this.formation = formation;
        this.promotion = promotion;
    }

    public String getFormation() {
        return formation;
    }

    public String getPromotion() {
        return promotion;
    }

    @Override
    public String toString() {
        return "Astrologue{" +
                "id='" + this.getId() + '\'' +
                ", denomination='" + this.getDenomination() + '\'' +
                ", presentation='" + this.getPresentation() + '\'' +
                ", formation='" + formation + '\'' +
                ", promotion='" + promotion + '\'' +
                '}';
    }
}
