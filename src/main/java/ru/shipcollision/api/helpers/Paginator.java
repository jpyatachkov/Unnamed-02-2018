package ru.shipcollision.api.helpers;

import ru.shipcollision.api.exceptions.PaginationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Вспомогательный класс для пагинации коллекции.
 */
public class Paginator<T> {

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

    public Paginator(List<T> objects, int offset, int limit) {
        this.objects = objects;
        this.offset = offset;
        this.limit = limit;
    }

    /**
     * Вспомогательный метод для подстановки параметров запроса.
     *
     * @param basePath    URI ресурса.
     * @param offsetParam Номер страницы.
     * @param limitParam  Количество записей на страницуе.
     * @return Ссылка на страницу с подставленными параметрами.
     */
    private String resolvePageLink(String basePath, int offsetParam, int limitParam) {
        return basePath + String.format("?offset=%d&limit=%d", offsetParam, limitParam);
    }

    /**
     * Метод пагинации.
     *
     * @return Страница с элементами или пустая коллекция.
     */
    public List<T> paginate() throws PaginationException {
        if (objects.isEmpty()) {
            return objects;
        }

        if (limit > MAX_LIMIT) {
            throw new PaginationException(String.format("Limit is not allowed to be more than %d", MAX_LIMIT));
        }

        final int startIdx = limit * (offset - 1);
        if (startIdx >= objects.size()) {
            throw new PaginationException();
        }

        int endIdx = startIdx + limit;
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
    public String resolvePrevPageLink(String basePath) {
        int newOffset = offset - limit;
        newOffset = (newOffset < 1) ? DEFAULT_OFFSET : newOffset;
        return (newOffset != offset) ? resolvePageLink(basePath, newOffset, limit) : null;
    }

    /**
     * Позволяет получить ссылку на следующую страницу.
     *
     * @param basePath URI ресурса.
     * @return Ссылка на страницу с подставленными параметрами или null.
     */
    public String resolveNextPageLink(String basePath) {
        int maxOffset = objects.size() / limit;
        maxOffset = (maxOffset == 0) ? DEFAULT_OFFSET : maxOffset;
        int newOffset = offset + 1;
        newOffset = (newOffset * limit < objects.size()) ? newOffset : maxOffset;
        return (newOffset != offset) ? resolvePageLink(basePath, newOffset, limit) : null;
    }
}
