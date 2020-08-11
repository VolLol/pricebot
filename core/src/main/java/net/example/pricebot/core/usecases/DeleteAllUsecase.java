package net.example.pricebot.core.usecases;


import net.example.pricebot.core.answerEntityes.AnswerEnum;
import net.example.pricebot.core.answerEntityes.DeleteAllAnswerEntity;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteAllUsecase {
    private static final Logger logger = LoggerFactory.getLogger(DeleteAllUsecase.class);

    private final SqlSessionFactory sqlSessionFactory;

    public DeleteAllUsecase(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;

    }

    public DeleteAllAnswerEntity execute(Long telegramUserId) {
        logger.info("Start execute delete usecase");
        SqlSession session = sqlSessionFactory.openSession();
        DeleteAllAnswerEntity answer = new DeleteAllAnswerEntity();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        goodsInfoMapper.deleteAll(telegramUserId);
        session.commit();
        session.close();
        answer.setAnswerEnum(AnswerEnum.SUCCESSFUL);
        answer.setMessageForUser("Watchlist has been cleared");
        return answer;
    }
}
