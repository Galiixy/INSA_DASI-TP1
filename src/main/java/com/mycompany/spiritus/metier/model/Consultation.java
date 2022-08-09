package com.mycompany.spiritus.metier.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Consultation {
    
    public enum Status {
        PENDING,
        REJECTED,
        VALIDATED,
        INPROGRESS,
        REALIZED
    };
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date date;
    private Status status;
    private String comment;

    @ManyToOne
    private Employee employee;
    @ManyToOne
    private Medium medium;
    @ManyToOne
    private Client client;
    

    public Consultation() {}

    public Consultation(Date date, Status status, Employee employee, Medium medium, Client client) {
        this.date = date;
        this.employee = employee;
        this.medium = medium;
        this.client = client;
        this.status = status;
    }
    
    public Consultation(Date date, Status status, Employee employee, Medium medium, Client client, String comment) {
        this.date = date;
        this.employee = employee;
        this.medium = medium;
        this.client = client;
        this.comment = comment;
        this.status = status;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    public void setClient(Client client) {
        this.client = client;
    }
    
    public Date getDate() {
        return date;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Medium getMedium() {
        return medium;
    }

    public Client getClient() {
        return client;
    }

    public Status getStatus() {
        return status;
    }
    
    public String getComment() {
        return comment;
    }
    
    public Long getId() {
        return id;
    }
  
    @Override
    public String toString() {
        return "Consultation{" +
                "id= " + id +
                ", date=" + date +
                ", status=" + status +
                ", comment=" + comment +
                ", employee=" + employee +
                ", medium=" + medium +
                ", client=" + client +
                '}';
    }

    public boolean equals(Object o){
        if(o instanceof Consultation){
            Consultation consultationToCompare = (Consultation) o;
            return this.id.equals(consultationToCompare.id);
        }
        return false;
    }
}
