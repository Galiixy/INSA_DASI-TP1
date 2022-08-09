package com.mycompany.spiritus.metier.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.util.List;

@Entity
public class Employee extends Person {

    public enum Gender {
        MALE,
        FEMALE
    }

    private boolean available;

    @OneToMany(mappedBy = "employee", cascade=CascadeType.PERSIST)
    private List<Consultation> consultations;

    @Version
    private Long version;

    private Gender gender;

    public Employee() {
    }

    public Employee(boolean available, String lastName, String firstName, String phone, String mail, String password, List<Consultation> consultations, Gender gender) {
        super(lastName, firstName, phone, mail, password);
        this.available = available;
        this.consultations = consultations;
        this.gender = gender;
    }

    public Gender getGender() {
        return gender;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }

    public Consultation addConsultation(Consultation consultation) {
        this.consultations.add(consultation);
        return consultation;
    }
      
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id= " + this.getId() +
                ", firstName= " + this.getFirstName()+
                ", lastName= " + this.getLastName()+
                ", mail= " + this.getMail() +
                ", available=" + available +
                ", version=" + version +
                ", gender=" + gender +
                '}';
    }

    public boolean equals(Employee o){
        if(o instanceof Employee){
            Employee employeeToCompare = (Employee) o;
            return this.getId().equals(employeeToCompare.getId());
        }
        return false;
    }

    public int hashCode() {
        return this.getMail().hashCode();
    }

}
