package net.example.pricebot.core.dto;

import lombok.Getter;
import lombok.Setter;
import net.example.pricebot.store.records.GoodsInfoRecord;

import java.util.List;


public class ShowAllAnswer {

    @Getter
    @Setter
    private List<GoodsInfoRecord> allRecords;
}