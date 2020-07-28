package net.example.pricebot.core.usecases;

import net.example.pricebot.core.dto.ShowAllAnswer;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShowAllGoodsUsecase {
    private static final Logger logger = LoggerFactory.getLogger(ShowAllGoodsUsecase.class);

    public static ShowAllAnswer execute(SqlSession session, String telegramId) {
        ShowAllAnswer answer = new ShowAllAnswer();

        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);

        List<GoodsInfoRecord> records = goodsInfoMapper.searchByTelegramUserId(telegramId);

        if (!records.isEmpty()) {
            logger.info("The user " + telegramId + " has following goods: ");
            for (GoodsInfoRecord model : records) {
                logger.info(model.toString());
            }
            answer.setAllRecords(records);
        } else {
            logger.info("The user " + telegramId + " has no goods");
        }
        return answer;
    }
}
