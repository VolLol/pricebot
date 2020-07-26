package net.example.pricebot.core.usecases;

import net.example.pricebot.core.dto.AnswerDto;
import net.example.pricebot.core.dto.AnswerEnum;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShowAllGoodsUsecase {
    private static final Logger logger = LoggerFactory.getLogger(ShowAllGoodsUsecase.class);

    public static AnswerDto execute(SqlSession session, String telegramId) {
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        List<GoodsInfoRecord> list = goodsInfoMapper.searchByTelegramUserId(telegramId);
        if (!list.isEmpty()) {
            logger.info("The user " + telegramId + " has following goods: ");
            for (GoodsInfoRecord model : list) {
                logger.info(model.toString());
            }
            return AnswerDto.builder().answerEnum(AnswerEnum.SUCCESSFUL).build();
        } else {
            logger.info("The user " + telegramId + " has no goods");
            return AnswerDto.builder().answerEnum(AnswerEnum.UNSUCCESSFUL).build();
        }
    }
}
