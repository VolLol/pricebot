package net.example.pricebot.store.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
public class GoodsHistoryPriceModel {

    @Getter
    @Setter
    private Long goodInfoId;

    @Getter
    @Setter
    private Integer price;

    @Getter
    @Setter
    private LocalDateTime createdAt;

    public GoodsHistoryPriceModel(Long goodInfoId, Integer price, LocalDateTime createdAt) {
        this.goodInfoId = goodInfoId;
        this.price = price;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "GoodsHistoryPriceModel: " +
                "goodInfoId=" + goodInfoId +
                ", price=" + price +
                ", createdAt=" + createdAt;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
