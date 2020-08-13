package net.example.pricebot.scheduler;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class GoodsPriceHistoryUpdaterScheduler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GoodsPriceHistoryUpdaterScheduler.class);
    private final SqlSessionFactory sqlSessionFactory;


    public GoodsPriceHistoryUpdaterScheduler(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;

    }

    @Override
    public void run() {
        while (true) {
            if (!isUpdatedDateContainInDB()) {
                logger.info("Start execute avito parsing");
                addCurrentDateToDb();
                parsingAvito();

            } else {
                logger.info("Waiting for a while");
                try {
                    Thread.sleep(10 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parsingAvito() {
        logger.info("Receive data about products that have not been deleted");
        SqlSession session = sqlSessionFactory.openSession();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        List<GoodsInfoRecord> records = goodsInfoMapper.getAllNotDeletedGoods();
        session.close();
        if (!records.isEmpty()) {
            for (GoodsInfoRecord r : records) {
                try {
                    String url = r.getProviderUrl();
                    Long goodId = r.getId();
                    GoodsInfoDTO goodsInfoDTO = new HarvesterAvito().getGoodsInfoByUrl(url);

                    GoodsHistoryPriceRecord goodsHistoryPriceRecord = GoodsHistoryPriceRecord.builder()
                            .goodsInfoId(goodId)
                            .createdAt(LocalDateTime.now())
                            .price(goodsInfoDTO.getPrice())
                            .build();
                    session = sqlSessionFactory.openSession();
                    GoodsHistoryPriceMapper goodsHistoryPriceMapper = session.getMapper(GoodsHistoryPriceMapper.class);
                    goodsHistoryPriceMapper.create(goodsHistoryPriceRecord);
                    session.commit();
                    session.close();
                    updateGoodsDate(goodId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            logger.info("There are no goods records");
        }
    }

    private void updateGoodsDate(Long goodId) {
        SqlSession session = sqlSessionFactory.openSession();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        goodsInfoMapper.updateDate(goodId);
        session.commit();
        session.close();
    }

    private void addCurrentDateToDb() {
        logger.info("Start adding date to db");
        SqlSession session = sqlSessionFactory.openSession();
        SchedulerMapper schedulerMapper = session.getMapper(SchedulerMapper.class);
        schedulerMapper.create(LocalDate.now());
        session.commit();
        session.close();
    }

    private boolean isUpdatedDateContainInDB() {
        logger.info("Start checking date exists in the database");
        LocalDate currentDate = LocalDate.now();
        SqlSession session = sqlSessionFactory.openSession();
        SchedulerMapper schedulerMapper = session.getMapper(SchedulerMapper.class);
        List<SchedulerRecord> records = schedulerMapper.showAllRecords();
        session.close();
        boolean isUpdatedToday = false;
        if (!records.isEmpty()) {
            for (SchedulerRecord r : records) {
                if (isDatesEquals(currentDate, r.getExecuteDate())) {
                    logger.info("Information was collected");
                    isUpdatedToday = true;

                } else {
                    logger.info("Information was'n collected ");
                }
            }
        } else {
            logger.info("No checks in the list");
        }
        return isUpdatedToday;
    }


    private boolean isDatesEquals(LocalDate currentDate, LocalDate record) {
        return currentDate.getYear() == record.getYear()
                && currentDate.getMonth() == record.getMonth()
                && currentDate.getDayOfMonth() == record.getDayOfMonth();
    }

}
