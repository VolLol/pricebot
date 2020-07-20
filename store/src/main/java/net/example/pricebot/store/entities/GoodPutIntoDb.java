package net.example.pricebot.store.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@Builder
public class GoodPutIntoDb {

    @Getter
    @Setter
    Long userId;

    @Getter
    @Setter
    Long id;

    @Getter
    @Setter
    String type;

    @Setter
    @Getter
    String url;

    @Getter
    @Setter
    java.sql.Date createDate;

    @Getter
    @Setter
    Date updateDate;


    public GoodPutIntoDb(Long userId, String type, String url) {
        this.userId = userId;
        this.type = type;
        this.url = url;
        this.createDate = new java.sql.Date(System.currentTimeMillis());
        this.updateDate = new java.sql.Date(System.currentTimeMillis());
    }
}
