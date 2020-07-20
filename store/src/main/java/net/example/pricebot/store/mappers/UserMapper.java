package net.example.pricebot.store.mappers;

import net.example.pricebot.store.UserEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface UserMapper {

    @Insert("Insert into users(id) values (#{id})")
    @Options(useGeneratedKeys = true)
    void addNewUserUser(UserEntity userEntity);

    @Select("SELECT * from users")
    List<Object> getAllUserId();
}
