package net.example.pricebot.store.records;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@ToString
public class GoodsInfoRecord {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String telegramUserId;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private Integer price;

    @Getter
    @Setter
    private String providerUrl;

    @Getter
    @Setter
    private String providerType;

    @Getter
    @Setter
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    private LocalDateTime createdAt;

    @Getter
    @Setter
    private Boolean isDeleted;


}
