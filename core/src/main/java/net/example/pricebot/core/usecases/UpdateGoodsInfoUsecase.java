package net.example.pricebot.core.usecases;

import net.example.pricebot.harvester.HarvesterAvito;
import net.example.pricebot.harvester.dto.GoodsInfoDTO;
import net.example.pricebot.store.mappers.GoodsHistoryPriceMapper;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.mappers.SchedulerMapper;
import net.example.pricebot.store.records.GoodsHistoryPriceRecord;
import net.example.pricebot.store.records.GoodsInfoRecord;
import net.example.pricebot.store.records.SchedulerRecord;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class UpdateGoodsInfoUsecase {

    private final SqlSessionFactory sqlSessionFactory;


    public UpdateGoodsInfoUsecase(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;

    }

    public void execute() throws IOException {
        LocalDate currentDate = LocalDate.now();
        SqlSession session = sqlSessionFactory.openSession();
        if (currentDate.isAfter(getLastExecuteDate(session))) {
            HarvesterAvito harvesterAvito = new HarvesterAvito();
            GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
            GoodsHistoryPriceMapper goodsHistoryPriceMapper = session.getMapper(GoodsHistoryPriceMapper.class);
            SchedulerMapper schedulerMapper = session.getMapper(SchedulerMapper.class);

            List<GoodsInfoRecord> allNotDeletedGoods = goodsInfoMapper.getAllNotDeletedGoods();
            for (GoodsInfoRecord record : allNotDeletedGoods) {
                GoodsInfoDTO harvesterGoodsInfoDto = harvesterAvito.getGoodsInfoByUrl(record.getProviderUrl());
                record.setUpdatedAt(currentDate.atStartOfDay());
                record.setPrice(harvesterGoodsInfoDto.getPrice());
                goodsInfoMapper.update(record.getId(), record.getPrice(), record.getUpdatedAt());//обновить всю запись

                goodsHistoryPriceMapper.create(GoodsHistoryPriceRecord.builder()
                        .price(record.getPrice())
                        .createdAt(currentDate.atStartOfDay())
                        .goodsInfoId(record.getId())
                        .build());

            }
            session.commit();
            schedulerMapper.create(currentDate);
            session.commit();
        }
        session.close();
    }

    private LocalDate getLastExecuteDate(SqlSession session) {
        SchedulerMapper schedulerMapper = session.getMapper(SchedulerMapper.class);
        SchedulerRecord schedulerRecord = schedulerMapper.getLastExecuteDate();
        if (schedulerRecord != null) {
            return schedulerRecord.getExecuteDate();
        }
        return LocalDate.now().minusDays(1);
    }

}
