package ru.pricehunt.assortment.dto;

import lombok.AllArgsConstructor;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {
    private int status;
    private String message;
    private long timeStamp;
}
