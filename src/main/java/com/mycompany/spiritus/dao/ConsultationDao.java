package com.mycompany.spiritus.dao;

import com.mycompany.spiritus.metier.model.*;

import javax.persistence.TypedQuery;
import java.util.List;

import static com.mycompany.spiritus.metier.model.Consultation.Status.INPROGRESS;
import static com.mycompany.spiritus.metier.model.Consultation.Status.PENDING;

public class ConsultationDao {

    public void create(Consultation consultation) {
        JpaUtil.obtenirContextePersistance().persist(consultation);
    }

    public void remove(Consultation consultation) {
        JpaUtil.obtenirContextePersistance().remove(consultation);
    }

    public Consultation update(Consultation consultation) {
        return JpaUtil.obtenirContextePersistance().merge(consultation);
    }

    public Consultation searchById(Long id) {
        return JpaUtil.obtenirContextePersistance().find(Consultation.class, id);
    }

    public List<Consultation> searchAll() {
        String s = "select c from Consultation c order by c.date asc";
        TypedQuery<Consultation> query = JpaUtil.obtenirContextePersistance().createQuery(s, Consultation.class);
        return query.getResultList();
    }

    public Consultation searchPendingConsultation(Employee employee) {
        String s = "select c from Consultation c where c.employee = ?1 and c.status = ?2";
        TypedQuery<Consultation> query = JpaUtil.obtenirContextePersistance().createQuery(s, Consultation.class);
        query.setParameter(1, employee);
        query.setParameter(2, PENDING);
        List<Consultation> resultList = query.getResultList();
        return resultList.size() == 0 ? null : resultList.get(0);
    }

    public Consultation searchInProgressConsultation(Employee employee) {
        String s = "select c from Consultation c where c.employee = ?1 and c.status = ?2";
        TypedQuery<Consultation> query = JpaUtil.obtenirContextePersistance().createQuery(s, Consultation.class);
        query.setParameter(1, employee);
        query.setParameter(2, INPROGRESS);
        List<Consultation> resultList = query.getResultList();
        return resultList.size() == 0 ? null : resultList.get(0);
    }

    public List<Consultation> getEmployeeConsultationHistory(Employee employee) {
        String s = "select c from Consultation c where c.employee = ?1";
        TypedQuery<Consultation> query = JpaUtil.obtenirContextePersistance().createQuery(s, Consultation.class);
        query.setParameter(1, employee);
        return query.getResultList();
    }
    
    public List<Consultation> getClientConsultationHistory(Client client) {
        String s = "select c from Consultation c where c.client = ?1";
        TypedQuery<Consultation> query = JpaUtil.obtenirContextePersistance().createQuery(s, Consultation.class);
        query.setParameter(1, client);
        return query.getResultList();
    }

    public List<Object[]> getTopXMediums(int x){
        String s = "select c.medium, count(c.medium) from Consultation c group by c.medium order by count(c.medium) desc";// where rownum <= ?1 group by c.medium order by count(c.medium) desc";
        TypedQuery<Object[]> query = JpaUtil.obtenirContextePersistance().createQuery(s, Object[].class).setMaxResults(x);
        return query.getResultList();
    }

    public Consultation getCurrentConsultationOfClient(Client client) {
        String s = "select c from Consultation c where c.client = ?1 and (c.status = ?2 or c.status = ?3)";
        TypedQuery<Consultation> query = JpaUtil.obtenirContextePersistance().createQuery(s, Consultation.class);
        query.setParameter(1, client);
        query.setParameter(2, INPROGRESS);
        query.setParameter(3, PENDING);
        List<Consultation> consultationsList = query.getResultList();
        return  consultationsList.size() > 0 ? consultationsList.get(0) : null;
    }

}
