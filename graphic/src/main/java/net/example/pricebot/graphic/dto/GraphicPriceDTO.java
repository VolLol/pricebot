package net.example.pricebot.graphic.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class GraphicPriceDTO {

    @Setter
    @Getter
    private String title;

    @Setter
    @Getter
    private List<GraphicRowItemDTO> items;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<GraphicRowItemDTO> getItems() {
        return items;
    }

    public void setItems(List<GraphicRowItemDTO> items) {
        this.items = items;
    }
}
