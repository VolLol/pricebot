package net.example.pricebot.harvester.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GoodsInfoDTO {
    @Setter
    private String title;

    @Setter
    private Integer price;

    @Setter
    private LocalDateTime updateAt;

    @Setter
    private GoodsInfoProvider provider;
}


