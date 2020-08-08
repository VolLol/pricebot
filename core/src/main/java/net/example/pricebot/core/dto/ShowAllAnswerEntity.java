package net.example.pricebot.core.dto;

import lombok.Getter;
import lombok.Setter;
import net.example.pricebot.store.records.GoodsInfoRecord;

import java.util.List;


public class ShowAllAnswerEntity {

    @Getter
    @Setter
    private String titleMessage;

    @Getter
    @Setter
    private List<GoodsInfoRecord> allRecords;
}
