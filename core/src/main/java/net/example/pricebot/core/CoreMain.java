package net.example.pricebot.core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import net.example.pricebot.core.dto.AnswerDto;
import net.example.pricebot.core.usecases.*;
import net.example.pricebot.store.DatabaseMigrationTools;
import net.example.pricebot.store.DatabaseSessionFactory;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;

public class CoreMain extends Application {
    private static final String driver = "org.postgresql.Driver";
    private static String JDBCUrl = "jdbc:postgresql://localhost:5432/pricebotdb";
    private static String username = "postgres";
    private static String password = "password";
    private static final Logger logger = LoggerFactory.getLogger(CoreMain.class);

    public static void main(String[] args) throws IOException {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String url = "https://www.avito.ru/sankt-peterburg/noutbuki/noutbuk_asus_shustryy_videokarta_1gb_1958417529";
        String telegramId = "telegram id";
        Long goodId = 3L;
        DatabaseMigrationTools.updateDatabaseVersion(JDBCUrl, username, password);
        PooledDataSource pooledDataSource = new PooledDataSource(driver, JDBCUrl, username, password);
        DatabaseSessionFactory databaseSessionFactory = new DatabaseSessionFactory(pooledDataSource);
        SqlSession session = databaseSessionFactory.getSession().openSession();
        AnswerDto answerAddRecord = AddRecordToDatabaseUsecase.execute(session, url);
        logger.info(answerAddRecord.toString(), AddRecordToDatabaseUsecase.class);
        AnswerDto answerShowAll = ShowAllGoodsUsecase.execute(session, telegramId);
        logger.info(answerShowAll.toString());
        AnswerDto answerCreateImage = CreateImageUsecase.execute(session, goodId, stage);
        logger.info(answerCreateImage.toString());
        Platform.exit();
    }


    private static Long searchGoodsIdByUrl(String url) {
        PooledDataSource pooledDataSource = new PooledDataSource(driver, JDBCUrl, username, password);
        DatabaseSessionFactory databaseSessionFactory = new DatabaseSessionFactory(pooledDataSource);
        SqlSession session = databaseSessionFactory.getSession().openSession();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        Long id = goodsInfoMapper.searchGoodByUrl(url);
        return id;
    }

}
