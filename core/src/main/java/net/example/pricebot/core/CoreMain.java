package net.example.pricebot.core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import net.example.pricebot.core.processors.TelegramPriceBotProcessor;
import net.example.pricebot.store.DatabaseMigrationTools;
import net.example.pricebot.store.DatabaseSessionFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;


import java.io.IOException;

public class CoreMain extends Application {
    private static final Logger logger = LoggerFactory.getLogger(CoreMain.class);
    private static final String driver = "org.postgresql.Driver";
    private static String JDBCUrl = "jdbc:postgresql://localhost:5432/pricebotdb";
    private static String username = "postgres";
    private static String password = "password";

    public static void main(String[] args) throws IOException {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        DatabaseMigrationTools.updateDatabaseVersion(JDBCUrl, username, password);
        PooledDataSource pooledDataSource = new PooledDataSource(driver, JDBCUrl, username, password);
        DatabaseSessionFactory databaseSessionFactory = new DatabaseSessionFactory(pooledDataSource);
        SqlSession session = databaseSessionFactory.getSession().openSession();
        try {
            telegramBotsApi.registerBot(new TelegramPriceBotProcessor(session,stage));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

        Platform.exit();
    }


}
