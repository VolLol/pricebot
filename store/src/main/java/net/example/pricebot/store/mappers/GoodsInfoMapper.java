package net.example.pricebot.store.mappers;

import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface GoodsInfoMapper {


    @Select("Select * from goods_info where telegram_user_id= #{telegramUserId} and is_deleted=false")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "telegramUserId", column = "telegram_user_id"),
            @Result(property = "price", column = "price"),
            @Result(property = "title", column = "title"),
            @Result(property = "providerUrl", column = "provider_url"),
            @Result(property = "providerType", column = "provider_type"),
            @Result(property = "createdAt", column = "create_at"),
            @Result(property = "updatedAt", column = "update_at"),
            @Result(property = "isDeleted", column = "is_deleted")
    })
    List<GoodsInfoRecord> searchByTelegramUserId(@Param("telegramUserId") Long telegramUserId);


    @Insert("Insert into goods_info(telegram_user_id,title,price,provider_url,provider_type,create_at,update_at)" +
            " values (#{telegramUserId},#{title},#{price},#{providerUrl},#{providerType},#{createdAt},#{updatedAt})")
    @Options(keyColumn = "id", useGeneratedKeys = true)
    void create(GoodsInfoRecord goodsInfoRecord);


    @Update("Update goods_info set is_deleted = true where telegram_user_id = #{telegramUserId}")
    void deleteAll(@Param("telegramUserId") Long telegramUserId);

    @Select("Select * from goods_info where id= #{id} and is_deleted=false limit 1")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "telegramUserId", column = "telegram_user_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "price", column = "price"),
            @Result(property = "providerUrl", column = "provider_url"),
            @Result(property = "providerType", column = "provider_type"),
            @Result(property = "createdAt", column = "create_at"),
            @Result(property = "updatedAt", column = "update_at"),
            @Result(property = "isDeleted", column = "is_deleted")
    })
    GoodsInfoRecord getById(@Param("id") Long id);


    @Select("Select id from goods_info where provider_url= #{url}")
    Long searchGoodByUrl(@Param("url") String url);

}
