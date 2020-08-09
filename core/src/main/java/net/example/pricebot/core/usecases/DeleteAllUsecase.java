package net.example.pricebot.core.usecases;


import net.example.pricebot.core.dto.AnswerEnum;
import net.example.pricebot.core.dto.CommonAnswerEntity;
import net.example.pricebot.core.dto.DeleteAllAnswerEntity;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteAllUsecase {
    private static final Logger logger = LoggerFactory.getLogger(DeleteAllUsecase.class);


    public static CommonAnswerEntity execute(Long telegramUserId, SqlSession session) {
        logger.info("Start execute delete usecase");
        CommonAnswerEntity commonAnswerEntity = new DeleteAllAnswerEntity();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        goodsInfoMapper.deleteAll(String.valueOf(telegramUserId));
        commonAnswerEntity.setAnswerEnum(AnswerEnum.SUCCESSFUL);
        commonAnswerEntity.setMessageForUser("Watchlist has been cleared");
        return commonAnswerEntity;
    }
}
