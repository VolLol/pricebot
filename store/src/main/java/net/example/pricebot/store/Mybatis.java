package net.example.pricebot.store;

import net.example.pricebot.store.entities.GoodReturnedFromDb;
import net.example.pricebot.store.entities.GoodPutIntoDb;
import net.example.pricebot.store.entities.GoodUpdateDateInDb;
import net.example.pricebot.store.entities.HistoryPricePutIntoDbEntity;
import net.example.pricebot.store.mappers.GoodMapper;
import net.example.pricebot.store.mappers.HistoryPriceMapper;
import net.example.pricebot.store.mappers.UserMapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

public class Mybatis {

    private static SqlSessionFactory sqlSessionFactory = null;

    public static void main(String[] args) {
        Properties prop = new Properties();
        prop.setProperty("driver", "org.postgresql.Driver");
        prop.setProperty("url", "jdbc:postgresql://localhost:5432/pricebotdb");
        prop.setProperty("user", "postgres");
        prop.setProperty("password", "password");

        StoreDataSourceFactory storeDataSourceFactory = new StoreDataSourceFactory();
        storeDataSourceFactory.setProperties(prop);


        DataSource dataSource = storeDataSourceFactory.getDataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(UserMapper.class);
        configuration.addMapper(GoodMapper.class);
        configuration.addMapper(HistoryPriceMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

        try (SqlSession session = sqlSessionFactory.openSession()) {




        } catch (NullPointerException e) {
            System.out.println("Catch nullPointer");
        }
    }


    private static void showAllGoodsFromUserFromDB(SqlSession session) {
        List<GoodReturnedFromDb> getGoodsByUserId = session.selectList("getGoodsByUserId", 1L);
        for (GoodReturnedFromDb o : getGoodsByUserId) {
            System.out.println(o.toString());
        }
    }

    private static void addGoodToTheDB(SqlSession session) {
        GoodPutIntoDb goodPutIntoDb = new GoodPutIntoDb(1L, "avito", "url here");
        session.insert("addGood", goodPutIntoDb);
        session.commit();

    }

    private static void updateDateInGoodsTable(SqlSession session) {
        Date newDate = new java.sql.Date(System.currentTimeMillis());
        GoodUpdateDateInDb goodUpdateDateInDb = new GoodUpdateDateInDb(5L, newDate);
        session.update("updateDate", goodUpdateDateInDb);
        session.commit();
    }

    private static void addEntryToHistoryPriceMethod(SqlSession session) {
        Date date = new Date(System.currentTimeMillis());
        HistoryPricePutIntoDbEntity historyPricePutIntoDbEntity = new HistoryPricePutIntoDbEntity(5L, 7000, date);
        session.insert("addToHistoryPrice", historyPricePutIntoDbEntity);
        session.commit();
    }


}
