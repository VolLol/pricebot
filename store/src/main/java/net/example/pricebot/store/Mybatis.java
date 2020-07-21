package net.example.pricebot.store;

import net.example.pricebot.store.mappers.GoodsHistoryPriceMapper;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.models.GoodsHistoryPriceModel;
import net.example.pricebot.store.models.GoodsInfoModel;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.time.LocalDateTime;
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
        configuration.addMapper(GoodsInfoMapper.class);
        configuration.addMapper(GoodsHistoryPriceMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            addGoodToDB(session);
            showAllGoodsFromUserFromDB(session);
            addGoodToHistoryPrice(session);
            showAllHistoryPriceById(session);
        } catch (NullPointerException e) {
            System.out.println("Catch nullPointer");
        }
    }


    private static void showAllGoodsFromUserFromDB(SqlSession session) {
        List<GoodsInfoModel> goods = session.selectList("getGoodsByTelegramUserId", "2837648726");
        for (GoodsInfoModel o : goods) {
            System.out.println(o.toString());
        }
    }

    private static void addGoodToDB(SqlSession session) {
        GoodsInfoModel good = new GoodsInfoModel("2837648726",
                "url here", "TYPE",
                false,
                LocalDateTime.now(), LocalDateTime.now());
        session.insert("addGood", good);
        session.commit();

    }


    private static void addGoodToHistoryPrice(SqlSession session) {
        GoodsHistoryPriceModel model = new GoodsHistoryPriceModel(3L, 4000, LocalDateTime.of(2020, 1, 12, 14, 12));
        session.insert("addGoodToHistory", model);
        session.commit();
    }

    private static void showAllHistoryPriceById(SqlSession session) {
        Long id = 3L;
        List<GoodsHistoryPriceModel> prices =
                session.selectList("getHistoryPriceById", id);
        for (GoodsHistoryPriceModel entity : prices) {
            System.out.println(entity.toString());
        }

    }

}
