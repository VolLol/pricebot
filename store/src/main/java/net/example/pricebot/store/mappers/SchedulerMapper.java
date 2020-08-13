package net.example.pricebot.store.mappers;

import net.example.pricebot.store.records.SchedulerRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface SchedulerMapper {

    @Select("SELECT * from goods_history_price_tasks ")
    List<SchedulerRecord> showAllRecords();


    @Insert("Insert into goods_history_price_tasks(execute_date) values (#{executeDate})")
    void create(LocalDate executeDate);

}
