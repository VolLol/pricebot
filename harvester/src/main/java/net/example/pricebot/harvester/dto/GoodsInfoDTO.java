package net.example.pricebot.harvester.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GoodsInfoDTO {
    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private Integer price;

    @Getter
    @Setter
    private LocalDateTime updateAt;

    @Getter
    @Setter
    private GoodsInfoProvider provider;


}


