package net.example.pricebot.store.entities;

import lombok.Getter;
import lombok.Setter;
import net.example.pricebot.store.UserEntity;

import java.util.Date;

public class GoodReturnedFromDb {

    @Getter
    @Setter
    UserEntity userEntity;
    @Getter
    @Setter
    Long id;
    @Getter
    @Setter
    String type;
    @Getter
    @Setter
    Date createDate;
    @Getter
    @Setter
    Date updateDate;
    @Getter
    @Setter
    String url;
    @Setter
    @Getter
    Boolean isDeleted;

    @Override
    public String toString() {
        return "GoodFromDb{" +
                "userId=" + userEntity +
                ", id=" + id +
                ", type='" + type + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", url='" + url + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
