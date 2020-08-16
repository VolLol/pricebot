package net.example.pricebot.core.usecases;

import net.example.pricebot.core.dto.DTOEnum;
import net.example.pricebot.core.dto.ShowAllGoodsFromWatchlistDTO;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShowAllGoodsFromWatchlistUsecase {
    private static final Logger logger = LoggerFactory.getLogger(ShowAllGoodsFromWatchlistUsecase.class);
    private final SqlSessionFactory sqlSessionFactory;

    public ShowAllGoodsFromWatchlistUsecase(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public ShowAllGoodsFromWatchlistDTO execute(Long telegramId) {
        logger.info("Start execute show all usecase");
        SqlSession session = sqlSessionFactory.openSession();
        ShowAllGoodsFromWatchlistDTO answer = new ShowAllGoodsFromWatchlistDTO();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        List<GoodsInfoRecord> records = goodsInfoMapper.searchByTelegramUserId(telegramId);
        Collection<String> messages = new ArrayList<>();
        if (!records.isEmpty()) {
            messages.add("You watching the following goods: ");
            for (GoodsInfoRecord record : records) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<b>Id : </b> " + record.getId() + "\n");
                stringBuilder.append("<b>Title : </b> " + record.getTitle() + "\n");
                stringBuilder.append("<b>Price : </b> " + record.getPrice() + "\n");
                messages.add(stringBuilder.toString());
            }
            answer.setAllGoods(messages);
        } else {
            logger.info("The user " + telegramId + " has no goods");
            answer.setMessageForUser("You are not watching any goods");
            answer.setDTOEnum(DTOEnum.SUCCESSFUL);

        }
        session.close();
        return answer;
    }
}
