package com.mycompany.spiritus.metier.model;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Client extends Person {
    
    private String address;
    private String totemAnimal;
    private String zodiacSign;
    private String chineseAstroSign;
    private String color;
    private Date birthDate;

    public Client() {
    }

    public Client(String lastName, String firstName, Date birthDate, String phone, String mail,
                  String password, String address, String totemAnimal,
                  String zodiacSign, String chineeseAstroSign, String color) {
        super(lastName, firstName, phone, mail, password);
        this.birthDate = birthDate;
        this.address = address;
        this.totemAnimal = totemAnimal;
        this.zodiacSign = zodiacSign;
        this.chineseAstroSign = chineeseAstroSign;
        this.color = color;
    }
    
     public Client(String lastName, String firstName, Date birthDate, String phone, String mail,
                  String password, String address) {
        super(lastName, firstName, phone, mail, password);
        this.birthDate = birthDate;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getTotemAnimal() {
        return totemAnimal;
    }

    public String getZodiacSign() {
        return zodiacSign;
    }

    public String getChineseAstroSign() {
        return chineseAstroSign;
    }

    public String getColor() {
        return color;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTotemAnimal(String totemAnimal) {
        this.totemAnimal = totemAnimal;
    }

    public void setZodiacSign(String zodiacSign) {
        this.zodiacSign = zodiacSign;
    }

    public void setChineseAstroSign(String chineeseAstroSign) {
        this.chineseAstroSign = chineeseAstroSign;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + this.getId() + '\'' +
                "lastname='" + this.getLastName() + '\'' +
                ", firstName='" + this.getFirstName() + '\'' +
                ", phone='" + this.getPhone() + '\'' +
                ", mail='" + this.getMail() + '\'' +
                ", address='" + address + '\'' +
                ", totemAnimal='" + totemAnimal + '\'' +
                ", zodiacSign='" + zodiacSign + '\'' +
                ", chineeseAstroSign='" + chineseAstroSign + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    public boolean equals(Object o){
        if(o instanceof Client){
            Client clientToCompare = (Client) o;
            return this.getId().equals(clientToCompare.getId());
        }
        return false;
    }
}
