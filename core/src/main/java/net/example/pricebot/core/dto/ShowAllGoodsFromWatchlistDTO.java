package net.example.pricebot.core.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

public class ShowAllGoodsFromWatchlistDTO extends CommonDTO {

    @Getter
    @Setter
    private Collection<String> allGoods;
}
