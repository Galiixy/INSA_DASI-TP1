package com.mycompany.spiritus.dao;

import com.mycompany.spiritus.metier.model.Employee;
import com.mycompany.spiritus.metier.model.Employee.Gender;
import com.mycompany.spiritus.metier.model.Person;

import javax.persistence.TypedQuery;
import java.util.List;

public class EmployeeDao {
    
    public void create(Person person) {
        JpaUtil.obtenirContextePersistance().persist(person);
    }

    public void remove(Person person) {
        JpaUtil.obtenirContextePersistance().remove(person);
    }

    public Person update(Person person) {
        return JpaUtil.obtenirContextePersistance().merge(person);
    }

    public Employee searchById(Long id) {
        return (Employee) JpaUtil.obtenirContextePersistance().find(Person.class, id);
    }

    public List<Employee> searchAll() {
        String s = "select c from Person c order by c.firstName asc";
        TypedQuery query = JpaUtil.obtenirContextePersistance().createQuery(s, Person.class);
        return query.getResultList();
    }

    public List<Employee> searchAllEmployees() {
        String s = "select e from Employee e";
        TypedQuery<Employee> query = JpaUtil.obtenirContextePersistance().createQuery(s, Employee.class);
        return query.getResultList();
    }

    public Employee searchByMail(String mail) {
        String s = "select c from Employee c where c.mail = '" + mail + "'";
        TypedQuery<Employee> query = JpaUtil.obtenirContextePersistance().createQuery(s, Employee.class);
        List<Employee> resultList = query.getResultList();
        return resultList.size() == 0 ? null : (Employee) resultList.get(0);
    }

    public List<Employee> getAvailableEmployeeSmallestRank(Gender gender) {
        String s = "select e from Employee e "
                + "where e.gender = ?1"
                + " and e.available = true ";
        TypedQuery<Employee> query = JpaUtil.obtenirContextePersistance().createQuery(s, Employee.class);
        query.setParameter(1, gender);
        return query.getResultList();
    }

    public List<Object[]> getNbClientsByEmployee() {
        String s = "select c.employee, count(distinct c.client) from Consultation c group by c.employee";
        TypedQuery<Object[]> query = JpaUtil.obtenirContextePersistance().createQuery(s, Object[].class);
        return query.getResultList();
    }

}
