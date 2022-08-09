package com.mycompany.spiritus.dao;

import com.mycompany.spiritus.metier.model.Client;
import com.mycompany.spiritus.metier.model.Employee;
import com.mycompany.spiritus.metier.model.Person;

import javax.persistence.TypedQuery;
import java.util.List;

public class PersonDao {
    
    public void create(Person person) {
        JpaUtil.obtenirContextePersistance().persist(person);
    }

    public void remove(Person person) {
        JpaUtil.obtenirContextePersistance().remove(person);
    }

    public Person update(Person person) {
        return JpaUtil.obtenirContextePersistance().merge(person);
    }

    public Person searchById(Long id) {
        return JpaUtil.obtenirContextePersistance().find(Person.class, id);
    }

    public List<Person> searchAll() {
        String s = "select c from Person c order by c.firstName asc";
        TypedQuery query = JpaUtil.obtenirContextePersistance().createQuery(s, Person.class);
        return query.getResultList();
    }

    public List<Employee> searchAllEmployees() {
        String s = "select e from Employee e";
        TypedQuery<Employee> query = JpaUtil.obtenirContextePersistance().createQuery(s, Employee.class);
        return query.getResultList();
    }

    public Person searchByMail(String mail) {
        String s = "select c from Person c where c.mail = '" + mail + "'";
        TypedQuery<Person> query = JpaUtil.obtenirContextePersistance().createQuery(s, Person.class);
        List<Person> resultList = query.getResultList();
        return resultList.size() == 0 ? null : resultList.get(0);
    }

    public List<Employee> getTopEmployees() {
        String s = "select c.employee from Consultation c group by c.employee order by count(c.employee) desc";
        TypedQuery<Employee> query = JpaUtil.obtenirContextePersistance().createQuery(s, Employee.class);
        return query.getResultList();
    }

    public List<Client> getAllClients() {
        String s = "SELECT c FROM Client c";
        TypedQuery<Client> query = JpaUtil.obtenirContextePersistance().createQuery(s, Client.class);
        return (List<Client>) query.getResultList();
    }

}
