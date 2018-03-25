package ru.shipcollision.api.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.exceptions.PaginationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = PaginationServiceImpl.class
)
@DisplayName("Тест сервиса пагинации")
public class PaginationServiceImplTest {

    @Autowired
    private PaginationServiceImpl<Object> paginationService;

    private static Stream<Arguments> provideCorrectPaginationData() {
        return Stream.of(
                Arguments.of(List.of(1, 2, 3), 1, 10, List.of(1, 2, 3)),
                Arguments.of(List.of(1, 2, 3), 1, 100, List.of(1, 2, 3)),
                Arguments.of(List.of(1, 2, 3), 1, 1, List.of(1)),
                Arguments.of(List.of(1, 2, 3), 2, 1, List.of(2)),
                Arguments.of(List.of(1, 2, 3), 2, 2, List.of(3))
        );
    }

    private static Stream<Arguments> provideIncorrectPaginationData() {
        return Stream.of(
                Arguments.of(List.of(1, 2, 3), 4, 10),
                Arguments.of(List.of(1, 2, 3), 4, PaginationServiceImpl.MAX_LIMIT + 1),
                Arguments.of(List.of(1, 2, 3), -1, 10),
                Arguments.of(List.of(1, 2, 3), 1, -10),
                Arguments.of(List.of(1, 2, 3), -1, -10)
        );
    }

    @SuppressWarnings("MagicNumber")
    private static Stream<Arguments> provideLongListSizes() {
        return Stream.of(
                Arguments.of(10000),
                Arguments.of(978)
        );
    }

    private static Stream<Arguments> provideDataForLinkResolving() {
        return Stream.of(
                Arguments.of(
                        List.of(1, 2, 3, 4, 5),
                        2,
                        1,
                        "base",
                        String.format("/?offset=%d&limit=%d", 1, 1),
                        String.format("/?offset=%d&limit=%d", 3, 1)
                ),
                Arguments.of(
                        List.of(1, 2, 3, 4, 5),
                        2,
                        1,
                        "base/",
                        String.format("/?offset=%d&limit=%d", 1, 1),
                        String.format("/?offset=%d&limit=%d", 3, 1)
                ),
                Arguments.of(
                        List.of(1, 2, 3, 4, 5),
                        2,
                        1,
                        "",
                        String.format("/?offset=%d&limit=%d", 1, 1),
                        String.format("/?offset=%d&limit=%d", 3, 1)
                ),
                Arguments.of(
                        List.of(1, 2, 3, 4, 5),
                        1,
                        1,
                        "",
                        null,
                        String.format("/?offset=%d&limit=%d", 2, 1)
                ),
                Arguments.of(
                        List.of(1, 2, 3, 4, 5),
                        5,
                        1,
                        "",
                        String.format("/?offset=%d&limit=%d", 4, 1),
                        null
                ),
                Arguments.of(
                        List.of(1, 2, 3, 4, 5),
                        1,
                        10,
                        "",
                        null,
                        null
                )
        );
    }

    @Test
    @DisplayName("пагинация пустого массива даст пустой массив")
    public void testEmptyListPagination() {
        paginationService.setObjects(new ArrayList<>());
        paginationService.setOffset(PaginationServiceImpl.DEFAULT_OFFSET);
        paginationService.setLimit(PaginationServiceImpl.DEFAULT_LIMIT);

        Assertions.assertEquals(new ArrayList<>(), paginationService.paginate());
    }

    @ParameterizedTest
    @MethodSource("provideCorrectPaginationData")
    @DisplayName("пагинация на корректных данных корректна")
    public void testPaginates(List<Object> objects, int offset, int limit, List<Object> expected) {
        paginationService.setObjects(objects);
        paginationService.setOffset(offset);
        paginationService.setLimit(limit);

        Assertions.assertEquals(expected, paginationService.paginate());
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectPaginationData")
    @DisplayName("на некорректных данных бросается исключение")
    public void testThrowsExceptionOnIncorrectData(List<Object> objects, int offset, int limit) {
        paginationService.setObjects(objects);
        paginationService.setOffset(offset);
        paginationService.setLimit(limit);

        Assertions.assertThrows(PaginationException.class, () -> paginationService.paginate());
    }

    @ParameterizedTest
    @MethodSource("provideLongListSizes")
    @DisplayName("пагинация на длинной последовательности корректна")
    public void testPaginatesLongList(int longListSize) {
        final List<Object> longList = new ArrayList<>();

        for (int i = 0; i < longListSize; i++) {
            longList.add(i);
        }

        paginationService.setObjects(longList);
        paginationService.setOffset(PaginationServiceImpl.DEFAULT_OFFSET);
        paginationService.setLimit(PaginationServiceImpl.DEFAULT_LIMIT);

        final int expectedPagesCount = (longListSize % PaginationServiceImpl.DEFAULT_LIMIT == 0) ?
                longListSize / PaginationServiceImpl.DEFAULT_LIMIT : longListSize / PaginationServiceImpl.DEFAULT_LIMIT + 1;

        for (int offset = 0; offset < expectedPagesCount; offset++) {
            if (offset == expectedPagesCount - 1) {
                System.out.println("aaa");
            }

            paginationService.setOffset(offset + 1);

            final int firstIdx = offset * PaginationServiceImpl.DEFAULT_LIMIT;
            final int lastIdx = (offset + 1) * PaginationServiceImpl.DEFAULT_LIMIT;
            Assertions.assertEquals(
                    longList.subList(firstIdx, (lastIdx > longList.size()) ? longList.size() : lastIdx),
                    paginationService.paginate()
            );
        }
    }

    @ParameterizedTest
    @MethodSource("provideDataForLinkResolving")
    @DisplayName("ссылки на предыдущую и следующую страницу корректны")
    public void testResolvtPageLinks(List<Object> objects,
                                     int offset,
                                     int limit,
                                     String basePath,
                                     String expectedPrev,
                                     String expectedNext) {
        paginationService.setObjects(objects);
        paginationService.setOffset(offset);
        paginationService.setLimit(limit);

        Assertions.assertEquals(expectedPrev, paginationService.resolvePrevPageLink(basePath));
        Assertions.assertEquals(expectedNext, paginationService.resolveNextPageLink(basePath));
    }
}
