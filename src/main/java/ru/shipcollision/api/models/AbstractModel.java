package ru.shipcollision.api.models;

import ru.shipcollision.api.exceptions.NotFoundException;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Базовый класс моделей.
 */
@SuppressWarnings({"PublicField", "unused"})
public abstract class AbstractModel {

    /**
     * Потокобезопасный генератор ID.
     */
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    public @NotNull Long id;

    public AbstractModel() {
        this.id = ID_GENERATOR.getAndIncrement();
    }

    protected static AbstractModel findById(Map<Long, AbstractModel> collection, Long id) throws NotFoundException {
        if (collection.containsKey(id)) {
            return collection.get(id);
        }
        throw new NotFoundException(String.format("User with id %d not found", id));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !Objects.equals(getClass(), object.getClass())) {
            return false;
        }
        final AbstractModel other = (AbstractModel) object;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }
}
