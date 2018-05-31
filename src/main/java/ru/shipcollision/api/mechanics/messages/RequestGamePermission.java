package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.websockets.Message;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Запрос на ход.
 */
@SuppressWarnings("PublicField")
public class RequestGamePermission extends Message {

    public @NotNull Coordinates coords;

    public RequestGamePermission() {
    }

    public RequestGamePermission(@NotNull Coordinates coords) {
        this.coords = coords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequestGamePermission other = (RequestGamePermission) o;
        return Objects.equals(coords, other.coords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coords);
    }
}
