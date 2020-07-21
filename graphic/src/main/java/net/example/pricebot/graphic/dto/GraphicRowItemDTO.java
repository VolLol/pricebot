package net.example.pricebot.graphic.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class GraphicRowItemDTO {

    @Getter
    @Setter
    Integer price;


    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Getter
    @Setter
    LocalDateTime date;

    public GraphicRowItemDTO(Integer price, LocalDateTime date) {
        this.price = price;
        this.date = date;
    }
}
