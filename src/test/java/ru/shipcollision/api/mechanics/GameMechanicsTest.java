package ru.shipcollision.api.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.UserTestFactory;
import ru.shipcollision.api.mechanics.base.CellStatus;
import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.mechanics.models.Player;
import ru.shipcollision.api.mechanics.services.GameSessionService;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.websockets.RemotePointService;

import javax.validation.constraints.NotNull;
import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Тесты сценариев игровой механики")
public class GameMechanicsTest {

    private static final long PLAYER1_ID = 1;

    private static final long PLAYER2_ID = 2;

    private static final long PLAYERS_COUNT = 2;

    @SuppressWarnings("unused")
    @MockBean
    private MechanicsExecutor mechanicsExecutor;

    @SuppressWarnings("unused")
    @MockBean
    private RemotePointService remotePointService;

    @Mock
    private GameSession gameSession;

    @Autowired
    private GameSessionService gameSessionService;

    private GameMechanics gameMechanics;

    private @NotNull Player player1;

    private @NotNull Player player2;

    private int fieldDim;

    private int shipsCount;

    private static Coordinates generateRandomCoords(int bound) {
        final Random random = new Random();
        return new Coordinates(random.nextInt(bound), random.nextInt(bound));
    }

    private static List<List<CellStatus>> generateRandomField(int fieldDim, int shipsCount) {
        final List<List<CellStatus>> gameField = new ArrayList<>();
        for (int i = 0; i < fieldDim; i++) {
            final List<CellStatus> row = new ArrayList<>();
            for (int j = 0; j < fieldDim; j++) {
                row.add(CellStatus.EMPTY);
            }
            gameField.add(row);
        }

        final Set<Coordinates> busyCoordsSet = new HashSet<>();
        while (busyCoordsSet.size() != shipsCount) {
            final Coordinates coords = generateRandomCoords(shipsCount);

            if (busyCoordsSet.contains(coords)) {
                continue;
            }

            busyCoordsSet.add(coords);
            gameField.get(coords.getI()).set(coords.getJ(), CellStatus.BUSY);
        }

        return gameField;
    }

    @BeforeEach
    void setUp() {
        gameMechanics = Mockito.spy(new GameMechanics(gameSessionService));

        fieldDim = GameRulesHelper.getFieldDimForPlayers((int) PLAYERS_COUNT);
        shipsCount = GameRulesHelper.getShipsCountForPlayers((int) PLAYERS_COUNT);
        final User user1 = UserTestFactory.createRandomUserWithId(PLAYER1_ID);
        final User user2 = UserTestFactory.createRandomUserWithId(PLAYER2_ID);

        player1 = new Player(user1, generateRandomField(fieldDim, shipsCount), shipsCount, PLAYERS_COUNT);
        player2 = new Player(user2, generateRandomField(fieldDim, shipsCount), shipsCount, PLAYERS_COUNT);
    }

    @DisplayName("игра для двух игроков начинается")
    @Test
    void testStartGame() {
        gameMechanics.addWaiter(player1);
        gameMechanics.gmStep();
        Assertions.assertNull(gameSessionService.getPlayerSession(player1.getUserId()));

        gameMechanics.addWaiter(player2);
        gameMechanics.gmStep();

        final GameSession player1Session = gameSessionService.getPlayerSession(player1.getUserId());
        final GameSession player2Session = gameSessionService.getPlayerSession(player2.getUserId());

        Assertions.assertNotNull(player1Session);
        Assertions.assertNotNull(player2Session);
        // Проверяем, что объект сессии один и тот же.
        Assertions.assertEquals(player1Session, player2Session);
    }

    @DisplayName("игрок может сделать выстрел, когда его ход")
    @Test
    void testCanDoShotIfIsMoving() {
        startGame();
        gameMechanics.makeMove(generateRandomCoords(fieldDim), player1.getUserId());
        Assertions.assertNotEquals(player1, gameSessionService.getPlayerSession(player1.getUserId()));
    }

    private void startGame() {
        gameMechanics.addWaiter(player1);
        gameMechanics.addWaiter(player2);
        gameMechanics.gmStep();
    }
}
