package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.mechanics.base.CellStatus;
import ru.shipcollision.api.websockets.Message;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Запрос на присоединение к какой-нибудь комнате.
 */
@SuppressWarnings("PublicField")
public class JoinGame extends Message {

    public @NotNull List<List<CellStatus>> field;

    public @NotNull Long count;

    public int computeShipsCount() {
        return (int) field.stream()
                .mapToLong(fieldRow -> fieldRow.stream().filter(cellStatus -> cellStatus == CellStatus.BUSY).count())
                .sum();
    }
}
