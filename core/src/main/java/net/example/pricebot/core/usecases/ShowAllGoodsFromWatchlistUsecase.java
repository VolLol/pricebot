package net.example.pricebot.core.usecases;

import com.vdurmont.emoji.EmojiParser;
import net.example.pricebot.core.dto.DTOEnum;
import net.example.pricebot.core.dto.ShowAllGoodsFromWatchlistDTO;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShowAllGoodsFromWatchlistUsecase {
    private static final Logger logger = LoggerFactory.getLogger(ShowAllGoodsFromWatchlistUsecase.class);
    private final SqlSessionFactory sqlSessionFactory;
    private final String ID_EMOJI = EmojiParser.parseToUnicode(":id:");
    private final String TITLE_EMOJI = EmojiParser.parseToUnicode(":shopping_bags:");
    private final String PRICE_EMOJI = EmojiParser.parseToUnicode(":moneybag:");


    public ShowAllGoodsFromWatchlistUsecase(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public ShowAllGoodsFromWatchlistDTO execute(Long telegramId) {
        logger.info("Start execute show all usecase");
        SqlSession session = sqlSessionFactory.openSession();
        ShowAllGoodsFromWatchlistDTO answer = new ShowAllGoodsFromWatchlistDTO();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        List<GoodsInfoRecord> records = goodsInfoMapper.searchByTelegramUserId(telegramId);
        if (!records.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (GoodsInfoRecord record : records) {
                stringBuilder.append(ID_EMOJI + "<b> Id : </b> " + record.getId() + "\n");
                stringBuilder.append(TITLE_EMOJI + "<b> Title : </b> " + record.getTitle() + "\n");
                stringBuilder.append(PRICE_EMOJI + "<b> Price : </b> " + record.getPrice() + "\n\n\n");
            }
            answer.setMessageForUser(stringBuilder.toString());
            answer.setDTOEnum(DTOEnum.SUCCESSFUL);

        } else {
            logger.info("The user " + telegramId + " has no goods");
            answer.setMessageForUser("You are not watching any goods");
            answer.setDTOEnum(DTOEnum.UNSUCCESSFUL);

        }
        session.close();
        return answer;
    }
}
