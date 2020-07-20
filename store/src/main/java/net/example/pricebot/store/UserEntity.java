package net.example.pricebot.store;

import lombok.Getter;
import lombok.Setter;
import net.example.pricebot.store.entities.GoodPutIntoDb;

import java.util.ArrayList;
import java.util.List;


public class UserEntity {


    @Setter
    @Getter
    Long id;

    @Getter
    @Setter
    List<GoodPutIntoDb> goodsToDb = new ArrayList<GoodPutIntoDb>();


    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", goodToDb=" + goodsToDb.toString() +
                '}';
    }

}
