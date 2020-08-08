package net.example.pricebot.core.usecases;


import net.example.pricebot.core.dto.AnswerEnum;
import net.example.pricebot.core.dto.CommonAnswerEntity;
import net.example.pricebot.core.dto.DeleteAllAnswerEntity;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import org.apache.ibatis.session.SqlSession;

public class DeleteAllUsecase {


    public static CommonAnswerEntity execute(Long telegramUserId, SqlSession session) {
        CommonAnswerEntity commonAnswerEntity = new DeleteAllAnswerEntity();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        goodsInfoMapper.deleteAll(String.valueOf(telegramUserId));
        commonAnswerEntity.setAnswerEnum(AnswerEnum.SUCCESSFUL);
        commonAnswerEntity.setMessageForUser("Watchlist has been cleared");
        session.commit();
        return commonAnswerEntity;
    }
}
