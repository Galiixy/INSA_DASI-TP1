/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.spiritus.metier.service;

import com.mycompany.spiritus.dao.ClientDao;
import com.mycompany.spiritus.dao.JpaUtil;
import com.mycompany.spiritus.dao.PersonDao;
import com.mycompany.spiritus.metier.model.Client;
import com.mycompany.spiritus.metier.model.Employee;
import com.mycompany.spiritus.metier.model.Person;
import com.mycompany.spiritus.utils.AstroNetApi;
import com.mycompany.spiritus.utils.Message;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nikitaterekhov
 */
public class AccountService implements Service {
    
     public Person authenticatePerson(String mail, String password) {
        PersonDao personDao = new PersonDao();
        Person person;
        try {
            JpaUtil.creerContextePersistance();
            person = personDao.searchByMail(mail);
            if (!person.getPassword().equals(password)) {
                person = null;
            }   
            Logger.getAnonymousLogger().log(Level.INFO, "Account successfully autheticated");
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error account creation");
            JpaUtil.annulerTransaction();
            person = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        
        return person;
    }

    public Client getClientById(Long id) {
        ClientDao clientDao = new ClientDao();
        Client client;
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            client = clientDao.searchById(id);
            JpaUtil.validerTransaction();
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error account creation");
            JpaUtil.annulerTransaction();
            client = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }

        return client;
    }
            
     public Client createAccount(Client client) {
        ClientDao clientDao = new ClientDao();
        Client clientFound;
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            clientFound = clientDao.searchByMail(client.getMail());
            if (clientFound == null) {
                AstroNetApi api = new AstroNetApi();
                List <String> ProfilAstral = api.getProfil(client.getFirstName(), client.getBirthDate());
                client.setZodiacSign(ProfilAstral.get(0));
                client.setChineseAstroSign(ProfilAstral.get(1));
                client.setColor(ProfilAstral.get(2));
                client.setTotemAnimal(ProfilAstral.get(3));
                clientDao.create(client);
                Logger.getAnonymousLogger().log(Level.INFO, "Account successfully created");
            } else {
                client = null;
                Logger.getAnonymousLogger().log(Level.INFO, "Account already exists");
            }
            JpaUtil.validerTransaction();
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error account creation");
            JpaUtil.annulerTransaction();
            client = null;
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        
        if (client != null) {
            Message.envoyerMail("contact@spiritus.fr", client.getMail(), "Création du compte", "Votre compte a bien été créé!");
        }
        return client;
    }

    public Person addPerson(Person person) {
        PersonDao personDao = new PersonDao();
        Person checkPerson;
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            checkPerson = personDao.searchByMail(person.getMail());
            if (checkPerson == null) {
                personDao.create(person);
                Logger.getAnonymousLogger().log(Level.INFO, "Account successfully created");
            }
            JpaUtil.validerTransaction();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error account creation");
            System.out.println(ex.getMessage());
            JpaUtil.annulerTransaction();
            person = null;
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        if (person != null) {
            Message.envoyerMail("contact@spiritus.fr", person.getMail(), "Création du compte", "Votre compte a bien été créé!");
        }
        return person;
    }

     public boolean updateClientInfo(Client client){

        ClientDao clientDao = new ClientDao();
        boolean result = true;
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            Client clientCheck = clientDao.searchById(client.getId());
            if (clientCheck != null) {
                clientDao.update(client);
            }
        JpaUtil.validerTransaction();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error account update");
            JpaUtil.annulerTransaction();
            result = false;
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return result;
    }

    public List<Employee> getAllEmployees() {
        PersonDao personDao = new PersonDao();
        List<Employee> employees;
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            employees = personDao.searchAllEmployees();
            JpaUtil.validerTransaction();
            Logger.getAnonymousLogger().log(Level.INFO, "Getting number of employees done ");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error getting number of employees");
            JpaUtil.annulerTransaction();
            employees = null;
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return employees;
    }

    public List<Client> getAllClients() {
        PersonDao personDao = new PersonDao();
        List<Client> clients;
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            clients = personDao.getAllClients();
            JpaUtil.validerTransaction();
            Logger.getAnonymousLogger().log(Level.INFO, "Getting number of employees done ");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error getting number of employees");
            JpaUtil.annulerTransaction();
            clients = null;
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return clients;
    }

}
