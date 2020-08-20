package net.example.pricebot.core.usecases;


import net.example.pricebot.core.dto.DTOEnum;
import net.example.pricebot.core.dto.DeleteAllGoodsForCustomerDTO;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteAllGoodsForCustomerUsecase {
    private static final Logger logger = LoggerFactory.getLogger(DeleteAllGoodsForCustomerUsecase.class);

    private final SqlSessionFactory sqlSessionFactory;

    public DeleteAllGoodsForCustomerUsecase(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;

    }

    public DeleteAllGoodsForCustomerDTO execute(Long telegramUserId) {
        logger.info("Start execute delete usecase");
        SqlSession session = sqlSessionFactory.openSession();
        DeleteAllGoodsForCustomerDTO answer = new DeleteAllGoodsForCustomerDTO();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        goodsInfoMapper.deleteAll(telegramUserId);
        session.commit();
        session.close();
        answer.setDTOEnum(DTOEnum.SUCCESSFUL);
        answer.setMessageForUser("Watchlist has been cleared");
        return answer;
    }
}
