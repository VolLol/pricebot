package net.example.pricebot.core.usecases;

import net.example.pricebot.core.answerEntityes.AnswerEnum;
import net.example.pricebot.core.answerEntityes.ShowAllAnswerEntity;
import net.example.pricebot.store.DatabaseMigrationTools;
import net.example.pricebot.store.DatabaseSessionFactory;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShowAllGoodsUsecase {
    private static final Logger logger = LoggerFactory.getLogger(ShowAllGoodsUsecase.class);
    private final String driver = "org.postgresql.Driver";
    private final String JDBCUrl = "jdbc:postgresql://localhost:5432/pricebotdb";
    private final String username = "postgres";
    private final String password = "password";
    private final SqlSession session;

    public ShowAllGoodsUsecase() {
        DatabaseMigrationTools.updateDatabaseVersion(JDBCUrl, username, password);
        PooledDataSource pooledDataSource = new PooledDataSource(driver, JDBCUrl, username, password);
        DatabaseSessionFactory databaseSessionFactory = new DatabaseSessionFactory(pooledDataSource);
        session = databaseSessionFactory.getSession().openSession();
    }


    public ShowAllAnswerEntity execute(Long telegramId) {
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
        session.close();
        return answer;
    }
}
