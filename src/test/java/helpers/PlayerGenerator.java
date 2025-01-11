package helpers;

import com.github.javafaker.Faker;
import ru.inno.course.player.model.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerGenerator {

    public static Set<Player> generate(int count) {
        Faker faker = new Faker();
        Set<Player> playerSet = new HashSet<>();
        for (int i = 0; i < count; i++) {
            int id = i + 1;
            String nick = faker.name().name();
            int points = faker.number().numberBetween(0, 100);
            boolean isOnline = faker.bool().bool();
            Player newPlayer = new Player(id, nick, points, isOnline);
            playerSet.add(newPlayer);
        }
        return playerSet;
    }
}
