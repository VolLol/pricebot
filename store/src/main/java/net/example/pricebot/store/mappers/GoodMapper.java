package net.example.pricebot.store.mappers;

import net.example.pricebot.store.entities.GoodReturnedFromDb;
import net.example.pricebot.store.entities.GoodPutIntoDb;
import net.example.pricebot.store.entities.GoodUpdateDateInDb;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface GoodMapper {

    @Select("Select * from goods")
    @Results({
            @Result(property = "userId", column = "userid"),
            @Result(property = "id", column = "id"),
            @Result(property = "createDate", column = "createdate"),
            @Result(property = "updateDate", column = "updatedate"),
            @Result(property = "url", column = "url")
    })
    List<GoodReturnedFromDb> getAllGoods();


    @Select("Select * from goods where userid= #{userid} and isdeleted=false")
    @Results({
            @Result(property = "userId", column = "userid"),
            @Result(property = "id", column = "id"),
            @Result(property = "createDate", column = "createdate"),
            @Result(property = "updateDate", column = "updateDate"),
            @Result(property = "url", column = "url"),
            @Result(property = "isDeleted", column = "isdeleted")
    })
    List<GoodReturnedFromDb> getGoodsByUserId(@Param("userid") Long userId);

    @Insert("Insert into goods(userId,url,type,createdate,updatedate)" +
            " values (#{userId},#{url},#{type},#{createDate},#{updateDate})")
    @Options(keyColumn = "id", useGeneratedKeys = true)
    void addGood(GoodPutIntoDb goodPutIntoDb);


    @Update("Update goods set updatedate = #{updateDate} where id = #{id}")
    void updateDate(GoodUpdateDateInDb goodUpdateDateInDb);

}
