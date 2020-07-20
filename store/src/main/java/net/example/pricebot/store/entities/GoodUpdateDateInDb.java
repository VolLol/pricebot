package net.example.pricebot.store.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class GoodUpdateDateInDb {

    @Getter
    @Setter
    Long id;

    @Getter
    @Setter
    Date updateDate;


    public GoodUpdateDateInDb(Long id, Date updateDate) {
        this.id = id;
        this.updateDate = updateDate;
    }
}
