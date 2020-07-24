package net.example.pricebot.store.mappers;

import net.example.pricebot.store.models.GoodsInfoModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface GoodsInfoMapper {


    @Select("Select * from goods_info where telegram_user_id= #{telegramUserId} and is_deleted=false")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "telegramUserId", column = "telegram_user_id"),
            @Result(property = "providerUrl", column = "provider_url"),
            @Result(property = "providerType", column = "provider_type"),
            @Result(property = "createdAt", column = "create_at"),
            @Result(property = "updatedAt", column = "update_at"),
            @Result(property = "isDeleted",column = "is_deleted"),
            @Result(property = "title",column = "title")
    })
    List<GoodsInfoModel> getGoodsByTelegramUserId(@Param("telegramUserId") String telegramUserId);


    @Insert("Insert into goods_info(telegram_user_id,provider_url,provider_type,create_at,update_at,title)" +
            " values (#{telegramUserId},#{providerUrl},#{providerType},#{createdAt},#{updatedAt},#{title})")
    @Options(keyColumn = "id", useGeneratedKeys = true)
    void addGood(GoodsInfoModel goodsInfoModel);


    @Update("Update goods_info set is_deleted = true where telegram_user_id = #{telegramUserId}")
    void deleteAll(@Param("telegramUserId") String telegramUserId);

    @Select("Select * from goods_info where id= #{id} and is_deleted=false")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "telegramUserId", column = "telegram_user_id"),
            @Result(property = "providerUrl", column = "provider_url"),
            @Result(property = "providerType", column = "provider_type"),
            @Result(property = "createdAt", column = "create_at"),
            @Result(property = "updatedAt", column = "update_at"),
            @Result(property = "isDeleted",column = "is_deleted"),
            @Result(property = "title",column = "title")
    })
    GoodsInfoModel getGoodById(@Param("id") Long id);

}
