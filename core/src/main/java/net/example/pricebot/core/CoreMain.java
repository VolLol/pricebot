package net.example.pricebot.core;

import javafx.application.Application;
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

public class CoreMain extends Application {
    private static final Logger logger = LoggerFactory.getLogger(CoreMain.class);
    private static final String driver = "org.postgresql.Driver";
    private static String JDBCUrl = "jdbc:postgresql://localhost:5432/pricebotdb";
    private static String username = "postgres";
    private static String password = "password";

    public static void main(String[] args)  {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage)  {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        DatabaseMigrationTools.updateDatabaseVersion(JDBCUrl, username, password);
        try {
            telegramBotsApi.registerBot(new TelegramPriceBotProcessor());
            logger.info("The bot was successfully launched");
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}