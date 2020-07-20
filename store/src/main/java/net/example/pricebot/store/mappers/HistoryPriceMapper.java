package net.example.pricebot.store.mappers;

import net.example.pricebot.store.entities.HistoryPricePutIntoDbEntity;
import org.apache.ibatis.annotations.Insert;

public interface HistoryPriceMapper {


    @Insert("Insert into historyprice(goodid,price,lastdate) values (#{goodId},#{price},#{lastDate})")
    void addToHistoryPrice(HistoryPricePutIntoDbEntity historyPricePutIntoDbEntity);

}
