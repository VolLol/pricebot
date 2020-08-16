package net.example.pricebot.graphic.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartRowItemDTO {

    @Getter
    @Setter
    Integer price;

    @Getter
    @Setter
    LocalDateTime date;


    public String getDateAsString() {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.YY"));
    }
}
