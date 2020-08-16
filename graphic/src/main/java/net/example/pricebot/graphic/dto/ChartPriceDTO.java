package net.example.pricebot.graphic.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartPriceDTO {

    @Setter
    @Getter
    private String title;

    @Setter
    @Getter
    private List<ChartRowItemDTO> items;

    @Getter
    @Setter
    private LocalDateTime startAt;

    @Getter
    @Setter
    private LocalDateTime finishAt;

    public String getStartAtAsStringDate() {
        return startAt.format(DateTimeFormatter.ofPattern("dd.MM"));
    }

    public String getFinishAtAsStringDate() {
        return finishAt.format(DateTimeFormatter.ofPattern("dd.MM"));
    }
}
