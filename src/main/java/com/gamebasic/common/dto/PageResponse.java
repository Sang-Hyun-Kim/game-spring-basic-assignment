package com.gamebasic.common.dto;


import lombok.Getter;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
public class PageResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;


    private PageResponse(
                List<T> content,
                int page,
                int size,
                long totalElements, // Long으로 하려했는데, Page.getTotalElements의 반환형이 long이네
                int totalPages,
                boolean last
            ){
        this.content = List.copyOf(content);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }
    // 정적 팩토리=>Page<?>
    public static <T> PageResponse<T> of(Page<?> page,
                                         List<T> content
            ){
        return new PageResponse<>(
                content, page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
// 제네릭으로 구현해서 코드 확장성과 재사용까지 고려하기
// 정적 팩토리 패턴의 생성자 사용, 앞으로 생길 목록형 조회를(만약) 추가한다면 활용할 수 있는 구현방식