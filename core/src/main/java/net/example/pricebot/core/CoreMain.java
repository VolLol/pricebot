package net.example.pricebot.core;

import javafx.application.Application;
import javafx.stage.Stage;
import net.example.pricebot.core.processors.TelegramPriceBotProcessor;
import net.example.pricebot.core.scheduler.GoodsPriceHistoryUpdaterScheduler;
import net.example.pricebot.store.DatabaseMigrationTools;
import net.example.pricebot.store.DatabaseSessionFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class CoreMain extends Application {
    private static final Logger logger = LoggerFactory.getLogger(CoreMain.class);

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        DatabaseMigrationTools.updateDatabaseVersion(PriceBotServerConfig.JDBC_URL, PriceBotServerConfig.DB_USERNAME, PriceBotServerConfig.DB_PASSWORD);
        PooledDataSource pooledDataSource = new PooledDataSource(PriceBotServerConfig.DB_DRIVER, PriceBotServerConfig.JDBC_URL, PriceBotServerConfig.DB_USERNAME, PriceBotServerConfig.DB_PASSWORD);
        DatabaseSessionFactory databaseSessionFactory = new DatabaseSessionFactory(pooledDataSource);
        pooledDataSource.setDefaultAutoCommit(true);
        try {
            telegramBotsApi.registerBot(new TelegramPriceBotProcessor(databaseSessionFactory.getSession()));
            logger.info("The bot was successfully launched");
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        GoodsPriceHistoryUpdaterScheduler goodsPriceHistoryUpdaterScheduler = new GoodsPriceHistoryUpdaterScheduler(databaseSessionFactory.getSession());
        Thread threadScheduler = new Thread(goodsPriceHistoryUpdaterScheduler);
        threadScheduler.setName("Scheduler");
        threadScheduler.start();

    }
}