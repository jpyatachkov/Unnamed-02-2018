package ru.shipcollision.api.services;

import org.springframework.stereotype.Service;
import ru.shipcollision.api.exceptions.PaginationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для пагинации коллекции.
 */
@Service
public class PaginationServiceImpl<T> implements PaginationService<T> {

    /**
     * Номер страницы по умолчанию - нумерация с 1.
     */
    public static final int DEFAULT_OFFSET = 1;

    /**
     * Количество записей на странице по умолчанию.
     */
    public static final int DEFAULT_LIMIT = 10;

    /**
     * Максимальное количество записей на странице.
     */
    public static final int MAX_LIMIT = 100;

    /**
     * Объекты для пагинации.
     */
    private List<T> objects;

    /**
     * Текущий номер страницы.
     */
    private int offset;

    /**
     * Текущее количество элементов на странице.
     */
    private int limit;

    /**
     * Вспомогательный метод для подстановки параметров запроса.
     *
     * @param basePath    URI ресурса.
     * @param offsetParam Номер страницы.
     * @param limitParam  Количество записей на страницуе.
     * @return Ссылка на страницу с подставленными параметрами.
     */
    private String resolvePageParams(String basePath, int offsetParam, int limitParam) {
        return String.format("/?offset=%d&limit=%d", offsetParam, limitParam);
    }

    /**
     * Метод пагинации.
     *
     * @return Страница с элементами или пустая коллекция.
     */
    @Override
    public List<T> paginate() {
        if (objects.isEmpty()) {
            return objects;
        }

        if (limit > MAX_LIMIT) {
            throw new PaginationException(String.format("Limit is not allowed to be more than %d", MAX_LIMIT));
        }

        final int startIdx = limit * (offset - 1);
        if (startIdx >= objects.size() || startIdx < 0) {
            throw new PaginationException();
        }

        int endIdx = startIdx + limit;
        if (endIdx < 0) {
            throw new PaginationException();
        }
        if (endIdx > objects.size()) {
            endIdx = objects.size();
        }

        final List<T> result = new ArrayList<>();
        for (int i = startIdx; i < endIdx; i++) {
            result.add(objects.get(i));
        }
        return result;
    }

    /**
     * Позволяет получить ссылку на предыдущую страницу.
     *
     * @param basePath URI ресурса.
     * @return Ссылка на страницу с подставленными параметрами или null.
     */
    @Override
    public String resolvePrevPageLink(String basePath) {
        int newOffset = offset - 1;
        newOffset = (newOffset < 1) ? DEFAULT_OFFSET : newOffset;

        return (newOffset != offset) ? resolvePageParams(basePath, newOffset, limit) : null;
    }

    /**
     * Позволяет получить ссылку на следующую страницу.
     *
     * @param basePath URI ресурса.
     * @return Ссылка на страницу с подставленными параметрами или null.
     */
    @Override
    public String resolveNextPageLink(String basePath) {
        int maxOffset = objects.size() / limit;
        maxOffset += (objects.size() % limit == 0) ? 0 : 1;
        maxOffset = (maxOffset == 0) ? DEFAULT_OFFSET : maxOffset;

        int newOffset = offset + 1;
        newOffset = (newOffset * limit < objects.size()) ? newOffset : maxOffset;

        return (newOffset != offset) ? resolvePageParams(basePath, newOffset, limit) : null;
    }

    @Override
    public void setObjects(List<T> objects) {
        this.objects = objects;
    }

    @Override
    public void setOffset(Integer offset) {
        this.offset = (offset != null) ? offset : DEFAULT_OFFSET;
    }

    @Override
    public void setLimit(Integer limit) {
        this.limit = (limit != null) ? limit : DEFAULT_LIMIT;
    }
}
