package net.example.pricebot.graphic.dto;

import java.time.LocalDateTime;

public class GraphicRowItemDTO {

    Integer price;

    LocalDateTime date;


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

    public GraphicRowItemDTO() {
    }

    public GraphicRowItemDTO(Integer price, LocalDateTime date) {
        this.price = price;
        this.date = date;
    }
}
