package ru.shipcollision.api.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import ru.shipcollision.api.models.AbstractModel;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Ответ API со списком игроков в порядке убывания рейтинга.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScoreboardResponseEntity {

    @JsonProperty("users")
    private @NotNull List<AbstractModel> users;

    @Nullable
    @JsonProperty("prevPage")
    private String prevPageLink;

    @Nullable
    @JsonProperty(value = "nextPage")
    private String nextPageLink;

    public ScoreboardResponseEntity(@NotNull List<AbstractModel> users, String prevPageLink, String nextPageLink) {
        this.users = users;
        this.prevPageLink = prevPageLink;
        this.nextPageLink = nextPageLink;
    }

    @SuppressWarnings("unused")
    public List<AbstractModel> getUsers() {
        return users;
    }

    @SuppressWarnings("unused")
    public void setUsers(@NotNull List<AbstractModel> users) {
        this.users = users;
    }

    @SuppressWarnings("unused")
    public String getPrevPageLink() {
        return prevPageLink;
    }

    @SuppressWarnings("unused")
    public void setPrevPageLink(String prevPageLink) {
        this.prevPageLink = prevPageLink;
    }

    @SuppressWarnings("unused")
    public String getNextPageLink() {
        return nextPageLink;
    }

    @SuppressWarnings("unused")
    public void setNextPageLink(String nextPageLink) {
        this.nextPageLink = nextPageLink;
    }
}
