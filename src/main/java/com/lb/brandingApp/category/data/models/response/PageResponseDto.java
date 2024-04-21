package com.lb.brandingApp.category.data.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder()
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponseDto <ContentItem> {
    private PagingMetadata metadata;
    private List<ContentItem> content;

    @Builder(access = AccessLevel.PUBLIC)
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PagingMetadata {
        @JsonProperty("page_size")
        private Integer pageSize;

        @JsonProperty("page_number")
        private Integer pageNumber;

        @JsonProperty("total_pages")
        private Integer totalPages;

        @JsonProperty("total_elements")
        private Long totalElements;
    }
}
