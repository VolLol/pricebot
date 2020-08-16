package net.example.pricebot.core.scheduler;

import net.example.pricebot.core.usecases.UpdateGoodsInfoUsecase;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GoodsPriceHistoryUpdaterScheduler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(GoodsPriceHistoryUpdaterScheduler.class);
    private final UpdateGoodsInfoUsecase updateGoodsInfoUsecase;

    public GoodsPriceHistoryUpdaterScheduler(SqlSessionFactory sqlSessionFactory) {
        this.updateGoodsInfoUsecase = new UpdateGoodsInfoUsecase(sqlSessionFactory);
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.updateGoodsInfoUsecase.execute();
                TimeUnit.HOURS.sleep(1);
            } catch (InterruptedException e) {
                break;
            } catch (IOException e) {
                logger.error("Something happened", e);
            }
        }


    }

}
