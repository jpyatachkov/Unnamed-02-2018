package ru.shipcollision.api.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Тест модели сообщения API.
 */
class ApiMessageTest {

    @ParameterizedTest
    @MethodSource("provideMessageContent")
    public void testActualContentEqualsExpected(String messageContent) {
        final ApiMessage message = new ApiMessage(messageContent);
        Assertions.assertEquals(message.message, messageContent);
    }

    private static Stream<Arguments> provideMessageContent() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("aaa")
        );
    }
}
