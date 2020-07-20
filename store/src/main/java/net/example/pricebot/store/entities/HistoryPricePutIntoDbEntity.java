package net.example.pricebot.store.entities;

import lombok.Getter;

import java.sql.Date;

public class HistoryPricePutIntoDbEntity {


    @Getter
    Long goodId;

    @Getter
    Integer price;

    @Getter
    Date lastDate;

    public HistoryPricePutIntoDbEntity(Long goodId, Integer price, Date lastDate) {
        this.goodId = goodId;
        this.price = price;
        this.lastDate = lastDate;
    }
}
