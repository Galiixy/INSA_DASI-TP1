package com.mycompany.spiritus;

import com.github.javafaker.Faker;
import com.mycompany.spiritus.dao.JpaUtil;
import com.mycompany.spiritus.metier.model.*;
import com.mycompany.spiritus.metier.service.AccountService;
import com.mycompany.spiritus.metier.service.PlanningService;
import com.mycompany.spiritus.utils.AstroNetApi;

import java.io.IOException;
import java.util.*;
import java.util.Random;
import java.util.logging.LogManager;

public class Main {

    public static void main (String[] argv) throws Exception {
        String commande ;
        Scanner clavier = new Scanner(System.in);
        System.out.println("Voulez vous voir les logs pour cette démo ? Y/N");
        
        commande = clavier.nextLine();
        if (commande.equals("N")){
            LogManager.getLogManager().reset();
        }
        JpaUtil.init();

        AccountService accountService = new AccountService();
        boolean sortie = true;
        
        Person person = null;
        
        
        FillDatabaseWithRandomData();

        while(sortie){
            System.out.println("Que voulez-vous faire ? Ecrire le chiffre");
            System.out.println("1. Créer un Client ");
            System.out.println("2. Connecter un Utilisateur ");
            System.out.println("3. Voir les fonctionnalités pour un Client");
            System.out.println("4. Voir les fonctionnalités pour un Employé");
            System.out.println("5. Déconnexion");
            System.out.println("6. Quitter");
            
            commande = clavier.nextLine();
            switch (commande){
                case "1":{
                    System.out.println("Saisir prenom");
                    String prenom = clavier.nextLine();
                    System.out.println("Saisir le nom");
                    String nom = clavier.nextLine();
                    System.out.println("Saisie de la date de naissance (2 chiffres)");
                    System.out.println("Saisie du jour");
                    String jour = clavier.nextLine();
                    System.out.println("Saisir le mois (2 chiffres)");
                    String mois = clavier.nextLine();
                    System.out.println("Saisir l'année");
                    String annee = clavier.nextLine();
                    Date date = null;
                    try {
                        date = new Date(Integer.parseInt(annee)-1900,Integer.parseInt(mois)-1,Integer.parseInt(jour));
                    }catch(Exception e){
                        System.out.println("Erreur format");
                        break;
                    }
                    System.out.println("Saisir le telephone");
                    String telephone = clavier.nextLine();
                    System.out.println("Saisir mail");
                    String mail = clavier.nextLine();
                    System.out.println("Saisir mot de passe");
                    String password = clavier.nextLine();
                    System.out.println("Saisir votre adresse postale");
                    String adresse = clavier.nextLine();
                    
                    Client client = new Client(nom, prenom, date,telephone, mail, password, adresse );
                    accountService.createAccount(client);
                    break;
                }
                case "2":{
                    System.out.println("Saisir votre mail");
                    String mail = clavier.nextLine();
                    System.out.println("Saisir votre password");
                    String password = clavier.nextLine();
                    person = accountService.authenticatePerson(mail, password);
                    if (person != null ){
                        System.out.println("Vous êtes connecté");
                    }else{
                        System.out.println("Erreur dans la connexion");
                    }   
                    break;
                }
                case "3":{
                    if (person != null && person instanceof Client){
                        fonctionsClient(person);   
                    }
                    else{
                        System.out.println("Veuillez vous connecter en tant que Client");
                    }
                    break;
                }
                case "4":{
                    if (person != null && person instanceof Employee){
                        fonctionsEmployee(person);
                    }
                    else{
                        System.out.println("Veuillez vous connecter en tant qu'Employé");
                    }
                    break;
                }
                case "5":{
                   if (person != null){
                        person = null;
                        System.out.println("Vous avez été déconnnecté");
                    }else{
                        System.out.println("Vous êtes déjà déconnecté");
                    }
                    break;
                }
                case "6":{
                    sortie = false;
                    break;
                }
                default:
                System.out.println("no match");
            }
        }

        clavier.close();
        TestTopXMediumService();
    }

    public static void TestTopXMediumService() {
        PlanningService planningService = new PlanningService();
        List<Object[]> objects = planningService.getTopXMediums(40);
        for (Object[] obj: objects) {
            System.out.println("top mediums : " + obj[0] + "\t" + obj[1]);
        }
    }

    public static void fonctionsClient(Person person){
        Scanner clavier = new Scanner(System.in);
        boolean sortie = true;
        PlanningService clientService = new PlanningService();
        Client client = (Client) person;
        
        while(sortie){
            System.out.println("Que voulez-vous faire ? Ecrire le chiffre");
            System.out.println("1. Voir Historique ");
            System.out.println("2. Voir médiums ");
            System.out.println("3. Demander Consultation");
            System.out.println("4. Voir son profil Astral");
            System.out.println("5. Quitter");
            
            String commande = clavier.nextLine();
            switch (commande){
                case "1":{
                   List <Consultation> historique = clientService.getClientConsultationHistory(client);
                    for(Consultation c : historique){
                        String affichageConsultation = "Consultation{" + c.getId() +
                                                        ", date=" + c.getDate() +
                                                        ", status=" + c.getStatus() +
                                                        ", medium=" + c.getMedium() +
                                                        '}';
                        System.out.println(affichageConsultation);
                    } 
                    break;
                }
                case "2":{
                    List <Medium> listeMediums = clientService.getAllMediums();
                    for(Medium m : listeMediums){
                        System.out.println(m.toString());
                    }
                    break;
                }
                case "3":{
                    System.out.println("Sélectionner un medium parmis ceux-là : ");
                    List <Medium> listeMediums = clientService.getAllMediums();
                    for(Medium m : listeMediums){
                        System.out.println(m.toString());
                    }
                    System.out.println("Ecrire l'ID ");
                    String ID = clavier.nextLine();
                    Medium m = clientService.getMedium(Long.parseLong(ID));
                    clientService.consultationRequest(m, client);
                    break;
                }
                case "4":{
                    System.out.println(person.toString());
                    break;
                }
                case "5":{
                    sortie = false;
                    break;
                }
            }
        } 
    }

    public static void fonctionsEmployee(Person person){
                Scanner clavier = new Scanner(System.in);
        boolean sortie = true;
        PlanningService employeService = new PlanningService();
        Employee emp = (Employee) person;
        
        while(sortie){
            System.out.println("Que voulez-vous faire ? Ecrire le chiffre");
            System.out.println("1. Voir Historique ");
            System.out.println("2. Voir Consultation en cours ");
            System.out.println("3. Aide prédiction");
            System.out.println("4. Top 5 Employé");
            System.out.println("5. Top 5 des médiums ");
            System.out.println("6. Voir son profil");
            System.out.println("7. Voir nombre client par employé");
            System.out.println("8. Terminer Consultation en cours");
            System.out.println("10. Quitter");
            
            String commande = clavier.nextLine();
            switch (commande){
                case "1":{
                   List <Consultation> historique = employeService.getEmployeeConsultationHistory(emp);
                    for(Consultation c : historique){
                        System.out.println(c.toString());
                    } 
                    break;
                }
                case "2":{
                    Consultation c = employeService.getPendingConsultationForEmployee(emp);
                    System.out.println(c.toString());
                    break;
                }
                case "3":{
                    Consultation c = employeService.getPendingConsultationForEmployee(emp);
                    Client client = c.getClient();
                    System.out.println("Combien pour l'amour :");
                    int amour = Integer.parseInt(clavier.nextLine());
                    System.out.println("Combien pour la santé :");
                    int santé = Integer.parseInt(clavier.nextLine());
                    System.out.println("Combien pour le travail :");
                    int travail = Integer.parseInt(clavier.nextLine());
                    List <String> Prediction = employeService.getPrediction(client, amour, santé, travail);
                    System.out.println(Prediction.toString());
                    break;
                }
                case "4":{
                    System.out.println(employeService.getNbClientsByEmployee().toString());
                    break;
                }
                case "5":{
                    List<Object[]> listTop = employeService.getTopXMediums(5);
                    String affichage =null;
                    for (Object[] result : listTop) {
                        affichage = result[0].toString() + result[1].toString();
                        System.out.println(affichage);
                    }
                    
                    break;
                }
                case "6":{
                    System.out.println(person.toString());
                    break;
                }
                case "7":{
                    System.out.println(employeService.getNbClientsByEmployee().toString());
                    break;
                }
                case "8":{
                    Consultation c = employeService.getPendingConsultationForEmployee(emp);
                    if (c!=null){
                        System.out.println("Ecrire le commentaire pour terminer la consultation : ");
                        String comment = clavier.nextLine();
                        employeService.finishConsultation(c, comment );
                    }else{
                        System.out.println("Aucune consultation en cours");
                    }
                    break;
                }
                case "10":{
                    sortie = false;
                    break;
                }
            }
            
        } 
    }
    public static void FillDatabaseWithRandomData() {
        int number = 100;
        FillEmployeesTable(number);
        FillClientsTable(number);
        FillMediumTable(10);
        FillRandomConsultations(number);
    }

    public static int generateInt (int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static void FillRandomConsultations (int nb) {
        AccountService accountService = new AccountService();
        PlanningService planningService = new PlanningService();
        List<Employee> employees =  accountService.getAllEmployees();
        List<Client> clients = accountService.getAllClients();
        List<Medium> mediums = planningService.getAllMediums();
        for (int i = 0; i < nb; ++i) {

            int empIndex = generateInt(0, employees.size() - 1);
            int clientIndex = generateInt(0, clients.size() - 1);
            int mediumIndex = generateInt(0, mediums.size() - 1);

            Date date = generateRandomDate();
            Consultation consultation = new Consultation(
                    date,
                    Consultation.Status.REALIZED,
                    employees.get(empIndex),
                    mediums.get(mediumIndex),
                    clients.get(clientIndex)
            );

            planningService.createConsultation(consultation);

        }
    }

    public static void FillMediumTable(int nbMediums) {
        PlanningService planningService = new PlanningService();
        for (int i = 0; i < nbMediums; ++i) {
            Medium med = generateRandomMedium();
            planningService.createMedium(med);
        }
    }

    public static void FillClientsTable(int nbClients) {
        AccountService accountService = new AccountService();
        for (int i = 0; i < nbClients; ++i) {
            Client client = getRandomClient();
            accountService.addPerson(client);
        }
    }

    public static void FillEmployeesTable(int nbEmployees) {
        AccountService accountService = new AccountService();
        Employee emp = null;
        for (int i = 0; i < nbEmployees; ++i) {
            emp = generateEmployee();
            accountService.addPerson(emp);
        }
        System.out.println("Pour la démo Employé : ");
        System.out.println(emp.getMail());
        System.out.println(emp.getPassword());

    }

    public static Medium generateRandomMedium() {
        Faker faker = new Faker();
        Random random = new Random();

        Medium medium = new Astrologue(
                faker.university().name(),
                faker.commerce().department(),
                faker.funnyName().name(),
                faker.lorem().characters().substring(0, 15),
                (random.nextBoolean()) ? Employee.Gender.FEMALE : Employee.Gender.MALE
        );

        Medium medium2 = new Spirite(
                faker.dragonBall().character(),
                faker.funnyName().name(),
                faker.lorem().characters().substring(0, 15),
                (random.nextBoolean()) ? Employee.Gender.FEMALE : Employee.Gender.MALE
        );

        Medium medium3 = new Cartomancien(
                faker.funnyName().name(),
                faker.lorem().characters().substring(0, 15),
                (random.nextBoolean()) ? Employee.Gender.FEMALE : Employee.Gender.MALE
        );

        int nb = generateInt(1, 3);

        switch (nb) {
            case 1 :
                return medium;
            case 2 :
                return medium2;
            default:
                return medium3;
        }
    }

    public static Date generateRandomDate() {
        int randomYear = generateInt(1980, 2010) - 1900;
        int randomMonth = generateInt(0, 11);
        int randomDay = generateInt(1, 28);

        return new Date(randomYear, randomMonth, randomDay);
    }

    public static Client getRandomClient () {
        Faker faker = new Faker();
        AstroNetApi ana = new AstroNetApi();

        Client client;
        try {
            String firstName = faker.name().firstName();
            Date birthDate = faker.date().birthday();
            List <String> infos = ana.getProfil(firstName, birthDate);
            String zodiacSign = infos.get(0);
            String chineeseSign = infos.get(1);
            String luckyCharmColor = infos.get(2);
            String totemAnimal = infos.get(3);
            client = new Client (
                    faker.name().lastName(),
                    firstName,
                    faker.date().birthday(),
                    faker.phoneNumber().phoneNumber(),
                    faker.internet().emailAddress(),
                    faker.internet().password(),
                    faker.address().fullAddress(),
                    totemAnimal,
                    zodiacSign,
                    chineeseSign,
                    luckyCharmColor
            );
        } catch (IOException ex) {
            client= null;
            System.out.println(ex.getMessage());
        }
        return client;

    }

    public static Employee generateEmployee() {
        Faker faker = new Faker();
        Random random = new Random();

        return new Employee(
                true,
                faker.name().lastName(),
                faker.name().firstName(),
                faker.phoneNumber().phoneNumber(),
                faker.internet().emailAddress(),
                faker.internet().password(),
                new ArrayList<>(),
                (random.nextBoolean()) ? Employee.Gender.FEMALE : Employee.Gender.MALE
        );
    }

}
