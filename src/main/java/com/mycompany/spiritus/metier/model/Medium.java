package com.mycompany.spiritus.metier.model;

import javax.persistence.*;
import com.mycompany.spiritus.metier.model.Employee.Gender;

@Entity
@Inheritance (strategy = InheritanceType.JOINED)
public abstract class Medium {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String denomination;
    private String presentation;
    private Gender gender;

    public Medium() {}

    public Medium(String denomination, String presentation, Gender gender) {
        this.denomination = denomination;
        this.presentation = presentation;
        this.gender = gender;
    }

    public String getDenomination() {
        return denomination;
    }

    public String getPresentation() {
        return presentation;
    }

    public Long getId() {
        return id;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    @Override
    public String toString() {
        return "Medium{" +
                "id=" + id +
                ", denomination='" + denomination + '\'' +
                ", presentation='" + presentation + '\'' +
                '}';
    }
}
