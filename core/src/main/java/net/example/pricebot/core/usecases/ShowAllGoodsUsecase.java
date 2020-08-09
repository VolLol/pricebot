package net.example.pricebot.core.usecases;

import net.example.pricebot.core.dto.AnswerEnum;
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
        logger.info("Start execute show all usecase");
        ShowAllAnswerEntity answer = new ShowAllAnswerEntity();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        List<GoodsInfoRecord> records = goodsInfoMapper.searchByTelegramUserId(telegramId);
        if (!records.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("You watching the following goods: \n");
            stringBuilder.append("<b>Id Title  Price</b>\n");
            for (GoodsInfoRecord record : records) {
                stringBuilder.append("<b>" + record.getId() + "</b> " + record.getTitle() + " <b>" + record.getPrice() + " </b>\n");
            }
            answer.setAnswerEnum(AnswerEnum.SUCCESSFUL);
            answer.setMessageForUser(stringBuilder.toString());
        } else {
            logger.info("The user " + telegramId + " has no goods");
            answer.setMessageForUser("You are not watching any goods");
            answer.setAnswerEnum(AnswerEnum.SUCCESSFUL);

        }
        return answer;
    }
}
