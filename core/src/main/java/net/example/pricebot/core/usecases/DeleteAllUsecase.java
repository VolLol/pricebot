package net.example.pricebot.core.usecases;


import net.example.pricebot.core.answerEntityes.AnswerEnum;
import net.example.pricebot.core.answerEntityes.DeleteAllAnswerEntity;
import net.example.pricebot.store.DatabaseMigrationTools;
import net.example.pricebot.store.DatabaseSessionFactory;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteAllUsecase {
    private static final Logger logger = LoggerFactory.getLogger(DeleteAllUsecase.class);

    private final String driver = "org.postgresql.Driver";
    private final String JDBCUrl = "jdbc:postgresql://localhost:5432/pricebotdb";
    private final String username = "postgres";
    private final String password = "password";
    private final SqlSession session;

    public DeleteAllUsecase() {
        DatabaseMigrationTools.updateDatabaseVersion(JDBCUrl, username, password);
        PooledDataSource pooledDataSource = new PooledDataSource(driver, JDBCUrl, username, password);
        pooledDataSource.setDefaultAutoCommit(true);
        DatabaseSessionFactory databaseSessionFactory = new DatabaseSessionFactory(pooledDataSource);
        session = databaseSessionFactory.getSession().openSession();
    }

    public DeleteAllAnswerEntity execute(Long telegramUserId) {
        logger.info("Start execute delete usecase");
        DeleteAllAnswerEntity answer = new DeleteAllAnswerEntity();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        goodsInfoMapper.deleteAll(telegramUserId);
        session.close();
        answer.setAnswerEnum(AnswerEnum.SUCCESSFUL);
        answer.setMessageForUser("Watchlist has been cleared");
        return answer;
    }
}
