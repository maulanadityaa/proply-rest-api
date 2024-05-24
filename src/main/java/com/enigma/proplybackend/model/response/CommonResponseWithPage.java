package com.enigma.proplybackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponseWithPage<T> {
    private Integer statusCode;
    private String message;
    private T data;
    private PagingResponse paging;
}
