package ru.shipcollision.api.services;

import java.util.List;

public interface PaginationService<T> {

    List<T> paginate();

    String resolvePrevPageLink(String basePath);

    String resolveNextPageLink(String basePath);

    void setObjects(List<T> objects);

    void setLimit(Integer limit);

    void setOffset(Integer offset);
}
