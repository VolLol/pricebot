package net.example.pricebot.store.mappers;

import net.example.pricebot.store.records.GoodsHistoryPriceRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface GoodsHistoryPriceMapper {

    @Select("Select * from goods_history_prices where goods_info_id= #{goodsInfoId} order by created_at desc limit 14")
    @Results({
            @Result(property = "goodsInfoId", column = "goods_info_id"),
            @Result(property = "price", column = "price"),
            @Result(property = "createdAt", column = "created_at"),
    })
    List<GoodsHistoryPriceRecord> searchTop14ByGoodsId(@Param("goodsInfoId") Long goodsInfoId);


    @Insert("Insert into goods_history_prices(goods_info_id, price,created_at)" +
            " values (#{goodsInfoId},#{price},#{createdAt})")
    void create(GoodsHistoryPriceRecord goodsHistoryPrice);

}
