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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final RequestGamePermission other = (RequestGamePermission) obj;
        return Objects.equals(coords, other.coords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coords);
    }
}
