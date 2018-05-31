package ru.shipcollision.api.mechanics.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.shipcollision.api.mechanics.base.CellStatus;
import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.models.User;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("PublicField")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Player {

    public @NotNull User user;

    public int score;

    public @NotNull List<List<CellStatus>> field;

    public int shipsCount;

    public Long wantedRoomPlayers;

    public Player(@NotNull User user,
                  @NotNull List<List<CellStatus>> field,
                  int shipCount,
                  Long wantedRoomPlayers) {
        this.user = user;
        this.score = 0;
        this.field = field;
        this.shipsCount = shipCount;
        this.wantedRoomPlayers = wantedRoomPlayers;
    }

    public CellStatus getCellStatus(Coordinates coords) {
        return field.get(coords.getI()).get(coords.getJ());
    }

    public void setCellStatus(Coordinates coords, CellStatus cellStatus) {
        field.get(coords.getI()).set(coords.getJ(), cellStatus);
    }

    public Long getUserId() {
        return user.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }
        Player player = (Player) obj;
        return Objects.equals(user.id, player.user.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(user);
    }
}
