package ru.shipcollision.api.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import ru.shipcollision.api.models.AbstractModel;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Ответ API со списком игроков в порядке убывания рейтинга.
 * Аннотация JsonInclude(JsonInclude.Include.NON_NULL) добавлена для того,
 * чтобы несуществующие страниы не показывались в ответе.
 */
@SuppressWarnings("PublicField")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScoreboardResponseEntity {

    @JsonProperty("users")
    public @NotNull List<AbstractModel> users;

    @Nullable
    @JsonProperty("prevPage")
    public String prevPageLink;

    @Nullable
    @JsonProperty(value = "nextPage")
    public String nextPageLink;

    public ScoreboardResponseEntity(@NotNull List<AbstractModel> users, String prevPageLink, String nextPageLink) {
        this.users = users;
        this.prevPageLink = prevPageLink;
        this.nextPageLink = nextPageLink;
    }
}
