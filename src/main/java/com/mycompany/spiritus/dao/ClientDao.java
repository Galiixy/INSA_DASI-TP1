package com.mycompany.spiritus.dao;

import com.mycompany.spiritus.metier.model.Client;
import com.mycompany.spiritus.metier.model.Person;

import javax.persistence.TypedQuery;
import java.util.List;

public class ClientDao {
    
    public void create(Person person) {
        JpaUtil.obtenirContextePersistance().persist(person);
    }

    public void remove(Person person) {
        JpaUtil.obtenirContextePersistance().remove(person);
    }

    public Client update(Person person) {
        return (Client) JpaUtil.obtenirContextePersistance().merge(person);
    }

    public Client searchById(Long id) {
        return (Client) JpaUtil.obtenirContextePersistance().find(Person.class, id);
    }

    public List<Person> searchAll() {
        String s = "select c from Person c order by c.firstName asc";
        TypedQuery query = JpaUtil.obtenirContextePersistance().createQuery(s, Person.class);
        return query.getResultList();
    }
    
    public Client searchByMail(String mail) {
        String s = "select c from Client c where c.mail = '" + mail + "'";
        TypedQuery<Client> query = JpaUtil.obtenirContextePersistance().createQuery(s, Client.class);
        List<Client> resultList = query.getResultList();
        return resultList.size() == 0 ? null : resultList.get(0);
    }
}
