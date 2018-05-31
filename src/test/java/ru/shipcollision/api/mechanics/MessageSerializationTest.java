package ru.shipcollision.api.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.shipcollision.api.mechanics.base.CellStatus;
import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.mechanics.messages.*;
import ru.shipcollision.api.websockets.Message;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

@DisplayName("Тесты сериализации игровых сообщений")
public class MessageSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Stream<Arguments> provideServerMessages() {
        return Stream.of(
                Arguments.of(new EnableScene(), "{}"),
                Arguments.of(new ErrorMessage("desc"), "{\"description\":\"desc\"}"),
                Arguments.of(
                        new GameMessage("error", "error"),
                        "{\"type\":\"error\",\"message\":\"error\"}"
                ),
                Arguments.of(new GameOver(false, 1), "{\"win\":false,\"score\":1}"),
                Arguments.of(new GameStarted(), "{}"),
                Arguments.of(
                        new MoveDone("message", new Coordinates(0, 0), CellStatus.DESTROYED, 1),
                        "{\"message\":\"message\",\"coord\":{\"i\":0,\"j\":0},\"cell\":\"DESTROYED\",\"score\":1}"
                )
        );
    }

    private static Stream<Arguments> provideClientMessages() {
        return Stream.of(
                Arguments.of(
                        "{\"count\":2,\"field\":["
                                + "[0,0,0,0,0,0,0,0,0,0],"
                                + "[0,0,0,0,0,0,0,0,0,0],"
                                + "[0,0,0,0,0,0,0,0,0,0],"
                                + "[0,0,0,0,0,0,0,0,0,0],"
                                + "[0,0,0,0,0,0,0,0,0,0],"
                                + "[0,0,0,0,0,0,0,0,0,0],"
                                + "[0,0,0,0,0,0,0,0,0,0],"
                                + "[0,0,0,0,0,0,0,0,0,0],"
                                + "[0,0,0,0,0,0,0,0,0,0],"
                                + "[0,0,0,0,0,0,0,0,0,0]"
                                + "]}",
                        JoinGame.class,
                        new JoinGame(
                                (long) 2,
                                Arrays.asList(
                                        Arrays.asList(
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY
                                        ),
                                        Arrays.asList(
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY
                                        ),
                                        Arrays.asList(
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY
                                        ),
                                        Arrays.asList(
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY
                                        ),
                                        Arrays.asList(
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY
                                        ),
                                        Arrays.asList(
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY
                                        ),
                                        Arrays.asList(
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY
                                        ),
                                        Arrays.asList(
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY
                                        ),
                                        Arrays.asList(
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY
                                        ),
                                        Arrays.asList(
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY, CellStatus.EMPTY,
                                                CellStatus.EMPTY, CellStatus.EMPTY
                                        )
                                )
                        )
                ),
                Arguments.of(
                        "{\"coords\":{\"i\":0,\"j\":0}}",
                        RequestGamePermission.class,
                        new RequestGamePermission(new Coordinates(0, 0))
                )
        );
    }

    @DisplayName("тест сериализации сообщений сервера")
    @ParameterizedTest
    @MethodSource("provideServerMessages")
    void testSeriveMessages(Message message, String expectedJson) {
        try {
            Assertions.assertEquals(expectedJson, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            Assertions.fail(e);
        }
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    @DisplayName("тест сериализации сообщений пользователя")
    @ParameterizedTest
    @MethodSource("provideClientMessages")
    void testClientMessages(String payload, Class<? extends Message> clazz, Message expectedMessage) {
        try {
            Assertions.assertEquals(expectedMessage, objectMapper.readValue(payload, clazz));
        } catch (IOException e) {
            Assertions.fail(e);
        }
    }
}
