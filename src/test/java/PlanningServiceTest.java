
import com.github.javafaker.Faker;
import com.mycompany.spiritus.dao.ConsultationDao;
import com.mycompany.spiritus.dao.JpaUtil;
import com.mycompany.spiritus.dao.MediumDao;
import com.mycompany.spiritus.dao.PersonDao;
import com.mycompany.spiritus.metier.model.Client;
import com.mycompany.spiritus.metier.model.Consultation;
import com.mycompany.spiritus.metier.model.Employee;
import com.mycompany.spiritus.metier.model.Medium;
import com.mycompany.spiritus.metier.service.PlanningService;
import com.mycompany.spiritus.metier.service.ServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/* * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.*/



/*
*
 *
 * @author nikitaterekhov
*/


public class PlanningServiceTest {

    PersonDao personDao;
    MediumDao mediumDao;
    ConsultationDao consultationDao;
    Client client;
    Medium medium;
    Employee employee;
    PlanningService planningService;
    Consultation consultation;
    List<Employee> employees;
    Faker faker;
    Random random;
    ObjectGenerator generator;

    @Before
    public void setup() {
        JpaUtil.init();

        personDao = new PersonDao();
        mediumDao = new MediumDao();
        consultationDao = new ConsultationDao();

        generator = new ObjectGenerator();
        faker = new Faker(new Locale("fr"));
        random = new Random();
        planningService = (PlanningService) ServiceFactory.buildService("Planinng");
        employees = new ArrayList<>();

        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();

            client = generator.generateRandomClient();
            personDao.create(client);


            int nbMediums = random.nextInt(20)+1;
            List <Medium> mediums= new ArrayList<Medium>();
            for (int j = 0; j < nbMediums; j++) {
                medium = generator.generateRandomMedium();
                mediums.add(medium);
                mediumDao.create(medium);
            }


            for (int i = 0; i < 20; i++) {

                employee = generator.generateRandomEmployee();
                if (i < 5) employee.setGender(Employee.Gender.FEMALE);
                else employee.setGender(Employee.Gender.MALE);
                employee.setAvailable(true);
                personDao.create(employee);

                int nbConsultations = random.nextInt(10) + 1;
                for (int j = 0; j < nbConsultations; j++) {

                    consultation = new Consultation(new Date(), Consultation.Status.REALIZED, employee,
                            mediums.get((i * j) % mediums.size()), client);
                    consultationDao.create(employee.addConsultation(consultation));
                }
                personDao.update(employee);
                employees.add(employee);
            }

            // TODO Helps to debug, remove after
            List<Employee> employeeList = personDao.searchAllEmployees();
            System.out.println("employee list size is " + employeeList.size());
            for (int i = 0; i < 10; i++) {
                System.out.println("LOCAL : Employee id = " + employees.get(i).getId() + " have "
                        + employees.get(i).getConsultations().size() + " consultations" + " and gender " + employees.get(i).getGender());
                System.out.println("DB : Employee id = " + employeeList.get(i).getId() + " have "
                        + employeeList.get(i).getConsultations().size() + " consultations " + " and gender " + employees.get(i).getGender());
            }
            System.out.println("size is " + employeeList.size());

            JpaUtil.validerTransaction();
            Logger.getAnonymousLogger().log(Level.INFO, "Employees successfully created");
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error employee creation");
            JpaUtil.annulerTransaction();
        } finally {
            JpaUtil.fermerContextePersistance();
        }
    }

    // TODO remove test employees and consultations from the database
    @After
    public void finish() {
        System.out.println("Please remove test samples from the database");
    }


    @Test
    public void requestConsultationShouldReturnTheSameGender() {

        Given:
        medium.setGender(Employee.Gender.FEMALE);

        When:
        consultation = planningService.consultationRequest(medium, client);

        Then:
        assertTrue("Consultant should have the same gender as Employee",
                consultation.getMedium().getGender() == consultation.getEmployee().getGender());
    }

    @Test
    public void requestConsultationShouldReturnTheLowestRankingEmployee() {


        Given:
        // sort employees by its consultations number
        employees.sort(Comparator.comparingInt(emp -> emp.getConsultations().size()));
        medium.setGender(Employee.Gender.FEMALE);

        When:
        consultation = planningService.consultationRequest(medium, client);

        Then:
        assertEquals("Attributed employee should have the lowest ranking",
                employees.get(0).getConsultations().size(),
                consultation.getEmployee().getConsultations().size());
    }

    @Test
    public void requestConsultationWithPendingConsultationShouldReturnTheLastConsultation() {

        Consultation pendingConsultation;

        Given:
        // sort employees by its consultations number
        medium.setGender(Employee.Gender.FEMALE);
        pendingConsultation = planningService.consultationRequest(medium, client);

        When:
        consultation = planningService.consultationRequest(medium, client);

        Then:
        assertEquals("Attributed employee should have the lowest ranking",
                pendingConsultation.getId(),
                consultation.getId());
    }

    @Test
    public void requestConsultationShouldReturnAnAvailableEmployee() {

        Given:
        medium.setGender(Employee.Gender.FEMALE);

        When:
        consultation = planningService.consultationRequest(medium, client);

        Then:
        assertTrue("Attributed employee should be available",
                consultation.getEmployee().isAvailable());
    }

    @Test
    public void getEmployeeConsultationHistoryShouldReturnHistoryBelongingToTheEmployee() {

        List<Consultation> consultationHistory;

        When:
        consultationHistory = planningService.getEmployeeConsultationHistory(employee);

        Then:
        assertTrue("Consultation history should belong to the employee",
                consultationHistory.containsAll(employee.getConsultations())
                        && employee.getConsultations().containsAll(consultationHistory));
    }

    @Test
    public void getNbClientsByEmployeeShouldReturnCorrectNumberOfClients() {

        HashMap<Employee, Long> employeesMap;

        employeesMap = planningService.getNbClientsByEmployee();

        Set<Client> clientSet = new HashSet<>();
        Long nbClients = 0L;
        Long clientsComparison;

        for (Employee emp : employees) {
            clientSet.clear();
            clientsComparison = -1L;
            for (Consultation cons : emp.getConsultations()) {
                if (!clientSet.contains(cons.getClient())) {
                    nbClients++;
                    clientSet.add(cons.getClient());
                }
            }
            for (Map.Entry<Employee, Long> entry: employeesMap.entrySet()) {
                if (entry.getKey().equals(emp)) {
                    clientsComparison = entry.getValue();
                    break;
                }
            }
            assertEquals("", nbClients, clientsComparison);
            nbClients = 0L;
        }

    }

    @Test
    public void getTopXMediumsShouldReturnTopXMediums() {
        List<Object[]> topXMediums;
        int x;
        int totalNumberOfMedium;
        Long nbConsultations = -1L;

        given:
        x = 5;
        totalNumberOfMedium = planningService.getNumberOfMediums();

        When:
        topXMediums = planningService.getTopXMediums(x);

        then:
        assertTrue(
                "The number of returned items must be lower than or equal to the total number of mediums",
                topXMediums.size() <= totalNumberOfMedium
        );
        assertTrue(
                "The number of returned items must be lower than or equal to the requested number of mediums",
                topXMediums.size() <= x
        );
        for (Object[] result : topXMediums) {
            assertTrue(
                    "The list should be sorted",
                    nbConsultations == -1 || nbConsultations >= (Long)result[1]
            );
            nbConsultations = (Long)result[1];
        }
    }

    @Test
    public void getPendingConsultationForEmployeeShouldReturnAnEmployee() {

        Consultation consActual;
        Consultation consExpected;

        When:
        consExpected = planningService.consultationRequest(medium, client);
        consActual = planningService.getPendingConsultationForEmployee(consExpected.getEmployee());

        Then:
        assertEquals("The method must return a pending consultation for an employee",
                consExpected.getId(),
                consActual.getId());
    }

}
