package com.qamet.book_store.rest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BookSpecDTO {
    private String bookName;
    private String bookDescription;
    private String authorName;
    private String publisherName;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
}
