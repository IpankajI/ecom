package com.ecom.productservice.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageCursor {
    private String after;
    private String before;
    private LocalDateTime time;
}
