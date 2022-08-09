/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.spiritus.dao;

import com.mycompany.spiritus.metier.model.Medium;
import com.mycompany.spiritus.metier.model.Person;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author nikitaterekhov
 */
public class MediumDao {
    
    public void create(Medium medium) {
        JpaUtil.obtenirContextePersistance().persist(medium);
    }

    public void remove(Medium medium) {
        JpaUtil.obtenirContextePersistance().remove(medium);
    }

    public Medium update(Medium medium) {
        return JpaUtil.obtenirContextePersistance().merge(medium);
    }

    public Medium searchById(Long id) {
        return JpaUtil.obtenirContextePersistance().find(Medium.class, id);
    }

    public List<Medium> searchAll() {
        String s = "select c from Medium c";
        TypedQuery query = JpaUtil.obtenirContextePersistance().createQuery(s, Person.class);
        return query.getResultList();
    }

    public int getNumberOfMediums() {
        String s = "select count(c) from Medium c";
        TypedQuery<Long> query = JpaUtil.obtenirContextePersistance().createQuery(s, Long.class);
        return query.getResultList().get(0).intValue();
    }
}
