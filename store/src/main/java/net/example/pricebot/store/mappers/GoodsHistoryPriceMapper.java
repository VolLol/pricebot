package net.example.pricebot.store.mappers;

import net.example.pricebot.store.models.GoodsHistoryPriceModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface GoodsHistoryPriceMapper {

    @Select("Select * from history_price where good_info_id= #{goodInfoId}")
    @Results({
            @Result(property = "good_info_id", column = "goodInfoId"),
            @Result(property = "price", column = "price"),
            @Result(property = "created_at", column = "createdAt"),
    })
    List<GoodsHistoryPriceModel> getHistoryPriceById(@Param("goodInfoId") Long goodInfoId);


    @Insert("Insert into history_price(good_info_id, price,created_at)" +
            " values (#{goodInfoId},#{price},#{createdAt})")
    void addGoodToHistory(GoodsHistoryPriceModel good);
}
