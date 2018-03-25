package ru.shipcollision.api.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@DisplayName("Тест модели сообщения API")
class ApiMessageTest {

    private static Stream<Arguments> provideMessageContent() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("aaa")
        );
    }

    @ParameterizedTest
    @MethodSource("provideMessageContent")
    @DisplayName("то, что мы записываем, совпадает с тем, что мы видим")
    public void testActualContentEqualsExpected(String messageContent) {
        final ApiMessage message = new ApiMessage(messageContent);
        Assertions.assertEquals(messageContent, message.message);
    }
}
