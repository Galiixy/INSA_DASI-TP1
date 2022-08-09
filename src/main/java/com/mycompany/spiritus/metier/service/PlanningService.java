package com.mycompany.spiritus.metier.service;

import com.mycompany.spiritus.dao.*;
import com.mycompany.spiritus.metier.model.Client;
import com.mycompany.spiritus.metier.model.Consultation;
import com.mycompany.spiritus.metier.model.Consultation.Status;
import com.mycompany.spiritus.metier.model.Employee;
import com.mycompany.spiritus.metier.model.Medium;
import com.mycompany.spiritus.utils.AstroNetApi;
import com.mycompany.spiritus.utils.Message;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanningService implements Service {
    
    public Consultation consultationRequest(Medium medium, Client client) {

        EmployeeDao employeeDao = new EmployeeDao();
        ConsultationDao consultationDao = new ConsultationDao();
        Consultation consultation = null;
        List<Employee> employees;
        Employee employee = null;
        
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();

            // check if the client already has requested a consultation
            for( Consultation pendingConsultation : consultationDao.getClientConsultationHistory(client)) {
                if (pendingConsultation.getStatus() == Status.PENDING) {
                    Logger.getAnonymousLogger().log(Level.INFO, "The client already has a pending reservation");
                    return pendingConsultation;
                }
            }
            
            // get the available employee with the smallest ranking
            employees = employeeDao.getAvailableEmployeeSmallestRank(medium.getGender());
            employees.sort(Comparator.comparingInt(emp -> emp.getConsultations().size()));
            employee = employees.get(0);
        
            // planify a meeting
            if (employee != null) {
                consultation = new Consultation(new Date(), Status.PENDING, employee, medium, client);
                employee.setAvailable(false);
                consultationDao.create(consultation);
                employee.addConsultation(consultation); 
                employeeDao.update(employee);
             
            }
            
            JpaUtil.validerTransaction();
            Logger.getAnonymousLogger().log(Level.INFO, "Consultation successfully created");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Consultation request rejected");
            JpaUtil.annulerTransaction();
            consultation = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        
        if (consultation != null) {
            Message.envoyerNotification(employee.getPhone(),
                        "Votre rendez-vous aura lieu le " + consultation.getDate().toString());
            Message.envoyerNotification(client.getPhone(), 
                    "Votre consultation aura lieu le " + consultation.getDate().toString());
        }
        
        return consultation;
    }
    
    public List<Employee> getTopEmployeesDesc() {

        PersonDao personDao = new PersonDao();
        List<Employee> topEmployees;
        try {
            JpaUtil.creerContextePersistance();
            topEmployees = personDao.getTopEmployees();
            Logger.getAnonymousLogger().log(Level.INFO, "Five top employees " + StringUtils.join(topEmployees, ", "));
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Top 5 employees not found");
            JpaUtil.annulerTransaction();
            topEmployees = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return topEmployees;
    }
            
    public List<Consultation> getEmployeeConsultationHistory(Employee employee) {

        ConsultationDao consultationDao = new ConsultationDao();
        List<Consultation> employeeHistoryList;
        try {
            JpaUtil.creerContextePersistance();
            employeeHistoryList = consultationDao.getEmployeeConsultationHistory(employee);
            Logger.getAnonymousLogger().log(Level.INFO, "Found a history of consultations : "
                    + StringUtils.join(employeeHistoryList, ", ")
                    + " for the employee : " + employee.toString());
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "History of consultations not found");
            JpaUtil.annulerTransaction();
            employeeHistoryList = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return employeeHistoryList;
    }

    public List<Consultation> getClientConsultationHistory(Client client) {

        ConsultationDao consultationDao = new ConsultationDao();
        List<Consultation> clientHistoryList;
        try {
            JpaUtil.creerContextePersistance();
            clientHistoryList = consultationDao.getClientConsultationHistory(client);
            Logger.getAnonymousLogger().log(Level.INFO, "Found a history of consultations : "
                    + StringUtils.join(clientHistoryList, ", ")
                    + " for the employee : " + client.toString());
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "History of consultations not found");
            JpaUtil.annulerTransaction();
            clientHistoryList = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return clientHistoryList;
    }


    public Consultation getPendingConsultationForEmployee(Employee employee) {

        ConsultationDao consultationDao = new ConsultationDao();
        Consultation consultation;
        try {
            JpaUtil.creerContextePersistance();
            consultation = consultationDao.searchPendingConsultation(employee);
            Logger.getAnonymousLogger().log(Level.INFO, "Pending consultation found for a user : " + employee.toString());
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Pending consultation not found");
            JpaUtil.annulerTransaction();
            consultation = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return consultation;
    }

    public Consultation getInProgressConsultationForEmployee(Employee employee) {

        ConsultationDao consultationDao = new ConsultationDao();
        Consultation consultation;
        try {
            JpaUtil.creerContextePersistance();
            consultation = consultationDao.searchInProgressConsultation(employee);
            Logger.getAnonymousLogger().log(Level.INFO, "InProgress consultation found for a user : " + employee.toString());
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "InProgress consultation not found");
            JpaUtil.annulerTransaction();
            consultation = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return consultation;
    }
    
    public boolean getDisponibilityEmployee(Employee employee) {
        return employee.isAvailable();
    }
    
    public void startConsultation(Consultation consultation) {

        ConsultationDao consultationDao = new ConsultationDao();
        
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            consultation.setStatus(Status.INPROGRESS);
            consultationDao.update(consultation);
            JpaUtil.validerTransaction();
            Logger.getAnonymousLogger().log(Level.INFO, "Consultation status successfully updated : in progress");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Consultation update rejected");
            JpaUtil.annulerTransaction();
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
    }
    
    public void finishConsultation(Consultation consultation, String comment) {
        ConsultationDao consultationDao = new ConsultationDao();
        EmployeeDao employeeDao = new EmployeeDao();
        
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            consultation.setComment(comment);
            consultation.setStatus(Status.REALIZED);
            Employee employee = consultation.getEmployee();
            employee.setAvailable(true);
            employeeDao.update(employee);
            consultationDao.update(consultation);
            JpaUtil.validerTransaction();
            Logger.getAnonymousLogger().log(Level.INFO, "Consultation status successfully updated : realized");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Consultation update rejected");
            JpaUtil.annulerTransaction();
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
    }

    public Medium createMedium(Medium medium) {
        MediumDao mediumDao = new MediumDao();

        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            mediumDao.create(medium);
            JpaUtil.validerTransaction();
            Logger.getAnonymousLogger().log(Level.INFO, "Medium successfully created");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Medium creation failed");
            medium = null;
            JpaUtil.annulerTransaction();
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }

        return medium;
    }

    public List<Medium> getAllMediums() {
        MediumDao mediumDao = new MediumDao();
        List<Medium> mediums;

        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            mediums = mediumDao.searchAll();
            JpaUtil.validerTransaction();
            Logger.getAnonymousLogger().log(Level.INFO, "Getting all mediums done");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Getting all mediums failed");
            mediums = null;
            JpaUtil.annulerTransaction();
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return mediums;
    }

    public Consultation createConsultation( Consultation consultation ) {
        ConsultationDao consultationDao = new ConsultationDao();

        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            consultationDao.create(consultation);
            JpaUtil.validerTransaction();
            Logger.getAnonymousLogger().log(Level.INFO, "Consultation created successfully");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Consultation creation failed");
            JpaUtil.annulerTransaction();
            consultation = null;
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return consultation;
    }

     public Employee getEmployee(Long id) {
        EmployeeDao employeeDao = new EmployeeDao();
        Employee employee;
        try {
            JpaUtil.creerContextePersistance();
            employee = employeeDao.searchById(id);
            Logger.getAnonymousLogger().log(Level.INFO, "Employee found successfully");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Employee not found");
            JpaUtil.annulerTransaction();
            employee = null;
        }
        return employee;
     }

    public Medium getMedium(Long id) {
        MediumDao mediumDao = new MediumDao();
        Medium medium;
        try {
            JpaUtil.creerContextePersistance();
            medium = mediumDao.searchById(id);
            Logger.getAnonymousLogger().log(Level.INFO, "Medium found successfully");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Medium not found");
            JpaUtil.annulerTransaction();
            medium = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return medium;
    }

    public Client getClient(Long id) {
        ClientDao clientDao = new ClientDao();
        Client client;
        try {
            JpaUtil.creerContextePersistance();
            client = clientDao.searchById(id);
            Logger.getAnonymousLogger().log(Level.INFO, "Client found successfully");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Client not found");
            JpaUtil.annulerTransaction();
            client = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return client;
    }

    public Consultation getClientCurrentConsultation(Client client) {
        ConsultationDao consultationDao = new ConsultationDao();
        Consultation consultation;
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            consultation = consultationDao.getCurrentConsultationOfClient(client);
            JpaUtil.validerTransaction();
            Logger.getAnonymousLogger().log(Level.INFO, "Consultation created successfully");
        }catch (Exception ex) {
            JpaUtil.annulerTransaction();
            consultation = null;
        }finally {
            JpaUtil.fermerContextePersistance();
        }
        return consultation;
    }

    public List<String> getPrediction(Client client, int loveNumber, int healthNumber, int jobNumber) {
        AstroNetApi api = new AstroNetApi();
        List<String> predictions;
        try {
            predictions = api.getPredictions(
                    client.getColor(),
                    client.getTotemAnimal(),
                    loveNumber,
                    healthNumber,
                    jobNumber);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getAnonymousLogger().log(Level.SEVERE, "Prediction error");
            predictions = null;
        }
        return predictions;
    }

    public HashMap<Employee, Long> getNbClientsByEmployee() {
        EmployeeDao employeeDao = new EmployeeDao();
        HashMap<Employee, Long> employees = new HashMap<>();
        try {
            JpaUtil.creerContextePersistance();
            List<Object[]> employeesList = employeeDao.getNbClientsByEmployee();
            for (Object[] result : employeesList) {
                employees.put((Employee) result[0], (Long) result[1]);
            }
            Logger.getAnonymousLogger().log(Level.INFO, "Employees with an associated number of clients found");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "No employees found");
            JpaUtil.annulerTransaction();
            employees = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return employees;
    }

    public List<Object[]> getTopXMediums(int x) {
        ConsultationDao consultationDao = new ConsultationDao();
        List<Object[]> topXMediums;
        try {
            JpaUtil.creerContextePersistance();
            topXMediums = consultationDao.getTopXMediums(x);
            Logger.getAnonymousLogger().log(Level.INFO, "Top " + x + " mediums : OK");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Top " + x + " mediums not found");
            JpaUtil.annulerTransaction();
            topXMediums = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return topXMediums;
    }

    public int getNumberOfMediums() {
        MediumDao mediumDao = new MediumDao();
        int result;
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            result = mediumDao.getNumberOfMediums();
        } catch (Exception ex) {
            JpaUtil.annulerTransaction();
            result = -1;
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return result;
    }
}
