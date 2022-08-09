import com.github.javafaker.Faker;
import com.mycompany.spiritus.metier.model.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class ObjectGenerator {

    private static Faker faker;
    private static Random random;

    public ObjectGenerator() {
        random = new Random();
        faker = new Faker(new Locale("fr"));
    }

    public Client generateRandomClient() {
        return new Client(
                faker.name().lastName(),
                faker.name().firstName(),
                faker.date().birthday(),
                faker.phoneNumber().phoneNumber(),
                faker.internet().emailAddress(),
                faker.internet().password(),
                faker.address().fullAddress()
        );
    }

    public Medium generateRandomMedium() {

        int mediumType = random.nextInt(2);
        Medium medium = switch (mediumType) {
            default -> new Astrologue(
                    faker.university().name(),
                    faker.commerce().department(),
                    faker.funnyName().name(),
                    faker.lorem().characters(),
                    (random.nextBoolean()) ? Employee.Gender.FEMALE : Employee.Gender.MALE
            );
            case 0 -> new Spirite(
                    faker.dragonBall().character(),
                    faker.funnyName().name(),
                    faker.lorem().characters(),
                    (random.nextBoolean()) ? Employee.Gender.FEMALE : Employee.Gender.MALE
            );
            case 1 -> new Cartomancien(
                    faker.funnyName().name(),
                    faker.lorem().characters(),
                    (random.nextBoolean()) ? Employee.Gender.FEMALE : Employee.Gender.MALE
            );
        };

        return medium;
    }

    public Employee generateRandomEmployee() {
        return new Employee(
                random.nextBoolean(),
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
