 /* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.*/



import com.mycompany.spiritus.dao.ClientDao;
import com.mycompany.spiritus.dao.EmployeeDao;
import com.mycompany.spiritus.dao.JpaUtil;
import com.mycompany.spiritus.dao.PersonDao;
import com.mycompany.spiritus.metier.model.Client;
import com.mycompany.spiritus.metier.model.Employee;
import com.mycompany.spiritus.metier.model.Person;
import com.mycompany.spiritus.metier.service.AccountService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/*
*
 *
 * @author nikitaterekhov
*/


public class AccountServiceTest {

    AccountService accountService;
    ObjectGenerator generator;
    PersonDao personDao;
    ClientDao clientDao;
    EmployeeDao employeeDao;
    Person personExpected;
    Client clientExpected;
    List<Employee> employees;
    List<Client> clients;
    Random random;
    Employee employee;
    Client client;

    @Before
    public void setup() {
        JpaUtil.init();

        generator = new ObjectGenerator();
        accountService = new AccountService();
        personDao = new PersonDao();
        clientDao = new ClientDao();
        employeeDao = new EmployeeDao();
        random = new Random();

        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();

            employees = new ArrayList<>();
            int nbEmployees = random.nextInt(10)+2;
            for (int i = 0; i < nbEmployees; i++) {
                employee = generator.generateRandomEmployee();
                employeeDao.create(employee);
                employees.add(employee);
            }

            personExpected = generator.generateRandomEmployee();
            personDao.create(personExpected);
            employees.add((Employee) personExpected);

            clients = new ArrayList<>();
            int nbClients = random.nextInt(10)+2;
            for (int i = 0; i < nbClients; i++) {
                client = generator.generateRandomClient();
                clientDao.create(client);
                clients.add(client);
            }

            clientExpected = generator.generateRandomClient();
            clientDao.create(clientExpected);
            clients.add(clientExpected);

            JpaUtil.validerTransaction();
            Logger.getAnonymousLogger().log(Level.INFO, "Employees successfully created");
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error employee creation");
            JpaUtil.annulerTransaction();
        } finally {
            JpaUtil.fermerContextePersistance();
        }
    }


    @Test
    public void authenticatePersonWithExistentMailMailShouldReturnAValidPerson() {

        Person personActual;

        When:
        personActual = accountService.authenticatePerson(personExpected.getMail(), personExpected.getPassword());

        Then:
        assertEquals("Account service must return a valid employee from an associated mail",
                personExpected.getId(), personActual.getId());
    }

    @Test
    public void authenticatePersonWithNotExistentMailMailShouldReturnNull() {

        Person personActual;
        String invalidMail = "invalid-mail@gmail.com";
        String password = "strongPassword";

        When:
        personActual = accountService.authenticatePerson(invalidMail, password);

        Then:
        assertEquals("Authetication service must return null if the mail does not exist",
                null, personActual);
    }

    @Test
    public void authenticatePersonWithAnInvalidPasswordShouldReturnNull() {

        Person personActual;
        String InvalidPassword = "strongPassword";

        When:
        personActual = accountService.authenticatePerson(personExpected.getMail(), InvalidPassword);

        Then:
        assertEquals("Authetication service must return null if the password is not valid",
                null, personActual);
    }

    @Test
    public void createClientShouldReturnAValidClient() {

        Client newClient;
        Client clientActual;

        When:
        newClient = generator.generateRandomClient();
        clientActual = accountService.createAccount(newClient);

        Then:
        assertEquals("The created user must be valid",
                newClient, clientActual);
    }

    @Test
    public void createClientWithAnInvalidMailShouldReturnNull() {

        Client clientActual;

        When:
        clientActual = accountService.createAccount(clientExpected);

        Then:
        assertEquals("The user must have a unique mail address",
                null, clientActual);
    }

    @Test
    public void updateClientInfoShouldUpdateAndReturnClient() {

        boolean isUpdated;

        When:
        isUpdated = accountService.updateClientInfo(clientExpected);

        Then:
        assertTrue("The client must have an existent mail", isUpdated);
    }

    @Test
    public void updateClientWithNotExistentMailShouldReturnFalse() {

        boolean isUpdated;
        Client newClient;

        When:
        newClient = generator.generateRandomClient();
        isUpdated = accountService.updateClientInfo(newClient);

        Then:
        assertFalse("Only existent client can be updated", isUpdated);
    }

    @Test
    public void getAllEmployeesShouldReturnAllEmployees() {

        List<Employee> employeeList;

        When:
        employeeList = accountService.getAllEmployees();

        Then:
        assertTrue("",
                employeeList.containsAll(employees)
                        && employees.containsAll(employeeList));
    }

    @Test
    public void getAllClientsShouldReturnAllClient() {

        List<Client> clientList;

        When:
        clientList = accountService.getAllClients();

        Then:
        assertTrue("",
                clientList.containsAll(clients)
                        && clients.containsAll(clientList));
    }

//    @Test
//    public void createClientWithAnInvalidMailShouldReturnNull() {
//
//        Person personActual;
//
//        When:
//        personActual = accountService.authenticatePerson(personExpected.getMail(), personExpected.getPassword());
//
//        Then:
//        assertEquals("Account service must return a valid employee from an associated mail",
//                personExpected.getId(), personActual.getId());
//    }

}
