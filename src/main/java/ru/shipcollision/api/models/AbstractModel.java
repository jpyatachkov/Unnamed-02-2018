package ru.shipcollision.api.models;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Базовый класс моделей.
 */
public abstract class AbstractModel {

    /**
     * Генератор ID.
     */
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    private @NotNull Long id;

    public AbstractModel() {
        this.id = ID_GENERATOR.getAndIncrement();
    }

    @SuppressWarnings("unused")
    protected static List<AbstractModel> findById(Map<Long, AbstractModel> collection, Long id) {
        final List<AbstractModel> result = new ArrayList<>();
        if (collection.containsKey(id)) {
            result.add(collection.get(id));
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final AbstractModel other = (AbstractModel) object;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

    public abstract void save();

    public Long getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(Long id) {
        this.id = id;
    }
}
