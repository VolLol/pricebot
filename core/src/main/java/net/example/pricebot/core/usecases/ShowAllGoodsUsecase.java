package net.example.pricebot.core.usecases;

import net.example.pricebot.core.dto.ShowAllAnswerEntity;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShowAllGoodsUsecase {
    private static final Logger logger = LoggerFactory.getLogger(ShowAllGoodsUsecase.class);

    public static ShowAllAnswerEntity execute(SqlSession session, String telegramId) {
        ShowAllAnswerEntity answer = new ShowAllAnswerEntity();

        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);

        List<GoodsInfoRecord> records = goodsInfoMapper.searchByTelegramUserId(telegramId);

        if (!records.isEmpty()) {
            answer.setAllRecords(records);
            answer.setTitleMessage("You watching the following goods: ");
        } else {
            logger.info("The user " + telegramId + " has no goods");
            answer.setTitleMessage("You are not watching any goods");
        }
        return answer;
    }
}
