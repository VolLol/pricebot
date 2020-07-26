package net.example.pricebot.store.records;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GoodsHistoryPriceRecord {

    @Getter
    @Setter
    private Long goodsInfoId;

    @Getter
    @Setter
    private Integer price;

    @Getter
    @Setter
    private LocalDateTime createdAt;


}
