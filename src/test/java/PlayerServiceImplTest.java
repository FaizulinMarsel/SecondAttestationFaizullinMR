import helpers.MyTestWatcher;
import helpers.Players;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import resolvers.PlayerServiceImplResolvers;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


@ExtendWith(PlayerServiceImplResolvers.class)
@DisplayName("Промежуточная аттестация 2. Написание юнит-тестов")
public class PlayerServiceImplTest {
    private String name = "John";
    private int id = 10;

    @Nested
    @DisplayName("Позитивные тесты")
    //В позитивных тестах не смог выполнить тесты, так как не понимаю их:
        //8.проверить корректность сохранения в файл
        //9.проверить корректность загрузки json-файла
        //-не потеряли записи
        //-не "побили" записи
    class PositiveTest{

        @Nested
        @DisplayName("Тесты добавления игроков. 1,3 и 4 ")
        class AddPlayerTests{


            @Test
            @DisplayName("Добавить игрока и проверить наличие в списке")
            void addPlayerAndCheckInList(@Players(0)PlayerService playerService){
                Collection <Player> players =playerService.getPlayers();
                int playerdId = playerService.createPlayer(name);
                Player player = playerService.getPlayerById(playerdId);
                assertThat(players).contains(player);
                assertAll(
                        () -> assertThat(player.getNick()).isEqualTo("John"),
                        () -> assertThat(player.getPoints()).isEqualTo(0),
                        () -> assertThat(player.isOnline()).isTrue()
                        );
            }

            @Test
            @DisplayName("Добавить игрока без существующего JSON-файла")
            void addPlayerWithoutJsonFile(@Players(0) PlayerService playerService) {
                Path filePath = Path.of("./data.json");
                assertThat(Files.exists(filePath)).isFalse();
                Collection <Player> players =playerService.getPlayers();
                int playerdId = playerService.createPlayer(name);
                Player player = playerService.getPlayerById(playerdId);
                assertThat(players).contains(player);
            }

            @Test
            @DisplayName("Добавить игрока с существующим JSON-файлом")
            void addPlayerWithJsonFile(@Players(1) PlayerService playerService) {
                Collection <Player> players =playerService.getPlayers();
                playerService.createPlayer(name);
                assertThat(players).hasSize(2);
            }
        }

        @Nested
        @DisplayName("Тесты начисления очков. 5 и 6")
        class AddPointsTests{

            @Test
            @DisplayName("Начислить баллы существующему игроку")
            void addPointsExistingPlayers(@Players(0)PlayerService playerService){
                int playerdId = playerService.createPlayer(name);
                Player player = playerService.getPlayerById(playerdId);
                playerService.addPoints(playerdId, 15);
                assertThat(player.getPoints()).isEqualTo(15);
            }

            @Test
            @DisplayName("Добавить очков поверх существующих")
            void addPointsPlayers(@Players(1) PlayerService playerService){
                int addPoints = 10;
                Player player = playerService.getPlayerById(1);
                int pointsPlayer = player.getPoints();
                playerService.addPoints(1,addPoints);
                assertThat(player.getPoints()).isEqualTo(addPoints + pointsPlayer);
            }
        }

        @Nested
        @DisplayName("Тесты получения игороков. 7, 10 и 11")
        class GetPlayersTests {

            @Test
            @DisplayName("Получить игрока по ID")
            void getPlayerId (@Players(0) PlayerService playerService) {
                int playerdId = playerService.createPlayer(name);
                Player player = playerService.getPlayerById(playerdId);
                assertThat(player.getId()).isEqualTo(1);
            }
            @Test
            @DisplayName("Проверка на уникальность ID")
            void checkUniquenessId(@Players(5) PlayerService playerService){
                playerService.deletePlayer(3);
                int playerId = playerService.createPlayer(name);
                Player player = playerService.getPlayerById(playerId);
                assertThat(player.getId()).isEqualTo(6);
            }
            @Test
            @DisplayName("Нет JSON файла. Запросить список игороков")
            void getListPlayers(@Players(0) PlayerService playerService) throws IOException {
                Path filePath = Path.of("./data.json");
                assertThat(Files.exists(filePath)).isFalse();
                Collection <Player> players =playerService.getPlayers();
                assertThat(players).isEmpty();
        }
        }

        @Nested
        @DisplayName("Граничные значения. 12")
        class LimitValuesTests{
            @Test
            @DisplayName("Создание игрока с 15 символами")
            void createPlayerNameSymbol15(@Players(0) PlayerService playerService){
                Player player = playerService.getPlayerById(playerService.createPlayer("QwertyQwertyQwe"));
                assertThat(player.getNick()).isEqualTo("QwertyQwertyQwe");
            }
        }
    }
    @Nested
    @DisplayName("Негативные тесты")
    class NegativeTest{
        @Nested
        @DisplayName("Удаление несуществующих игроков. 1")
        class DeletionTest{
            @Test
            @DisplayName("Удалить игрока,которого нет-удалить игрока 10,хотя последний-8")
            void deletionPlayer(@Players(8) PlayerService playerService){
                assertThatThrownBy(() -> playerService.deletePlayer(id))
                                        .isInstanceOf(NoSuchElementException.class)
                                        .hasMessageContaining("No such user: " + id);
            }
        }
        @Nested
        @DisplayName("Ошибки при создании игроков. 2, 4 и 12")
        class CreationErrorTests{
            @Test
            @DisplayName("Создать игрока с таким же именем")
            void createDuplicatePlayers(@Players(0) PlayerService playerService){
                playerService.createPlayer(name);
                assertThatThrownBy(() -> playerService.createPlayer(name))
                                        .isInstanceOf(IllegalArgumentException.class)
                                        .hasMessageContaining("Nickname is already in use: " + name);
            }
            @Test
            @DisplayName("Сохранить игрока с пустым ником")
            // Этот тест не проверить, так как у методе createPayer не обрабатывается случай, когда пустой ник.
            void savePlayerNullName(@Players(0) PlayerService playerService){
                assertThatThrownBy(() -> playerService.createPlayer(""))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Nickname is already in use: ");
            }

            @Test
            @DisplayName("Создание игрока в 16 символами")
            void createPlayerNameSymbol16(){
                //Этот кейс тоже не проверить, так как в методе создания игрока нет ограничений на количество символов
            }
        }
        @Nested
        @DisplayName("Ошибки модификации игроков. 3,5 и 6")
        class ModificationErrorsTests{
            @Test
            @DisplayName("Получить игрока по ID которого нет")
            void getPlayerNotId(@Players(3) PlayerService playerService){
                assertThatThrownBy(() -> playerService.getPlayerById(id))
                        .isInstanceOf(NoSuchElementException.class)
                        .hasMessageContaining("No such user: " + id);
            }
            @Test
            @DisplayName("Начислить отрицательное число очков")
            //В этом кейсе нет смысла, так как нет проверки на минусовое значение очков.
            void addPrivativePoints(@Players(0) PlayerService playerService){
                int playerId = playerService.createPlayer(name);
                playerService.addPoints(playerId, -10);
                Player player = playerService.getPlayerById(playerId);
                assertThat(player.getPoints()).isEqualTo(-10);
            }
            @Test
            @DisplayName("Накинуть очков игроку которого нет")
            void addPointsNotPlayers(@Players(1) PlayerService playerService){
                assertThatThrownBy(() -> playerService.addPoints(id, 1))
                        .isInstanceOf(NoSuchElementException.class)
                        .hasMessageContaining("No such user: " + id);
            }
        }
        // Тесты ниже тоже не проверить:
        // 7.Накинуть очков без указания id. Мы физически не можем этого сделать, так как в методе добавления очков указывается id
        // 8.Ввести невалидный id(String). IDEA не даст этого сделать
        // 9.Проверить загрузку системы с другим json-файлом. В реализации PlayerService не так такой возможности
        // 10.Начислить 1.5 баллаи гроку. Невозможно, так как в методе addPoints переменная int
        // 11.проверить корректность загрузки json-файла-есть дубликаты. Аналогично 9
    }
}
