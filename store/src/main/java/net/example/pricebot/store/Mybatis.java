package net.example.pricebot.store;

import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.models.GoodsHistoryPriceModel;
import net.example.pricebot.store.models.GoodsInfoModel;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSession;
import java.time.LocalDateTime;
import java.util.List;

public class Mybatis {
    private static String driver = "org.postgresql.Driver";
    private static String url = "jdbc:postgresql://localhost:5432/pricebotdb";
    private static String username = "postgres";
    private static String password = "password";


    public static void main(String[] args) {
        PooledDataSource pooledDataSource = new PooledDataSource(driver, url, username, password);
        DatabaseSessionFactory databaseSessionFactory = new DatabaseSessionFactory(pooledDataSource);
        SqlSession session = databaseSessionFactory.getSession().openSession();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        List<GoodsInfoModel> goodsList = goodsInfoMapper.getGoodsByTelegramUserId("2837648726");
        for (GoodsInfoModel model : goodsList) {
            System.out.println(model.toString());
        }

    }


    private static void showAllGoodsFromUserFromDB(SqlSession session) {
        List<GoodsInfoModel> goods = session.selectList("getGoodsByTelegramUserId", "2837648726");
        for (GoodsInfoModel o : goods) {
            System.out.println(o.toString());
        }
    }

    private static void addGoodToDB(SqlSession session) {
     /*   GoodsInfoModel good = new GoodsInfoModel("2837648726",
                "url here", "TYPE",
                false,
                LocalDateTime.now(), LocalDateTime.now());
        session.insert("addGood", good);
        session.commit();
*/
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

    private static void deleteAll(SqlSession session) {
        String telegramId = "2837648726";
        session.update("deleteAll", telegramId);
        session.commit();
    }

}
