package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.mechanics.base.CellStatus;
import ru.shipcollision.api.websockets.Message;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * Запрос на присоединение к какой-нибудь комнате.
 */
@SuppressWarnings("PublicField")
public class JoinGame extends Message {

    public @NotNull Long count;

    public @NotNull List<List<CellStatus>> field;

    public JoinGame() {
    }

    public JoinGame(Long count, List<List<CellStatus>> field) {
        this.count = count;
        this.field = field;
    }

    public int computeShipsCount() {
        return (int) field.stream()
                .mapToLong(fieldRow -> fieldRow.stream().filter(cellStatus -> cellStatus == CellStatus.BUSY).count())
                .sum();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final JoinGame other = (JoinGame) obj;
        return Objects.equals(count, other.count)
                && Objects.equals(field, other.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, field);
    }
}
