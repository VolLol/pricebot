package net.example.pricebot.core;

import net.example.pricebot.graphic.dto.GraphicPriceDTO;
import net.example.pricebot.graphic.dto.GraphicRowItemDTO;
import net.example.pricebot.harvester.HarvesterAvito;
import net.example.pricebot.harvester.dto.GoodsInfoDTO;
import net.example.pricebot.store.DatabaseSessionFactory;
import net.example.pricebot.store.mappers.GoodsHistoryPriceMapper;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.models.GoodsHistoryPriceModel;
import net.example.pricebot.store.models.GoodsInfoModel;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSession;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CoreMain {
    private static String driver = "org.postgresql.Driver";
    private static String JDBCUrl = "jdbc:postgresql://localhost:5432/pricebotdb";
    private static String username = "postgres";
    private static String password = "password";


    public static void main(String[] args) throws IOException {
        String url = "https://www.avito.ru/sankt-peterburg/noutbuki/noutbuk_core_i3_core_i5_core_i7_nvidia_ssd_1925067780";
        String telegramId = "telegram id";
        Long goodId = 20L;

        preparingDataForCreatingImage(goodId);
    }

    private static void addToDatabase(String url) throws IOException {
        HarvesterAvito harvesterAvito = new HarvesterAvito();
        GoodsInfoDTO goodsInfoDTO = harvesterAvito.getGoodsInfoByUrl(url);
        GoodsInfoModel goodsInfoModel = new GoodsInfoModel(
                "telegram id",
                url,
                "AVITO",
                false,
                goodsInfoDTO.getUpdateAt(),
                LocalDateTime.now(),
                goodsInfoDTO.getTitle()
        );
        PooledDataSource pooledDataSource = new PooledDataSource(driver, JDBCUrl, username, password);
        DatabaseSessionFactory databaseSessionFactory = new DatabaseSessionFactory(pooledDataSource);
        SqlSession session = databaseSessionFactory.getSession().openSession();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        try {
            goodsInfoMapper.addGood(goodsInfoModel);
        } catch (Exception e) {
            System.out.println("Already exist");
        }
        session.commit();
        session.close();

    }

    private static void showAllFromDBByTelegramId(String telegramId) {
        PooledDataSource pooledDataSource = new PooledDataSource(driver, JDBCUrl, username, password);
        DatabaseSessionFactory databaseSessionFactory = new DatabaseSessionFactory(pooledDataSource);
        SqlSession session = databaseSessionFactory.getSession().openSession();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        List<GoodsInfoModel> list = goodsInfoMapper.getGoodsByTelegramUserId(telegramId);
        if (!list.isEmpty()) {
            for (GoodsInfoModel model : list) {
                System.out.println(model.toString());
            }
        } else {
            System.out.println("You have no goods");
        }
        session.close();
    }

    private static void preparingDataForCreatingImage(Long goodId) {
        PooledDataSource pooledDataSource = new PooledDataSource(driver, JDBCUrl, username, password);
        DatabaseSessionFactory databaseSessionFactory = new DatabaseSessionFactory(pooledDataSource);
        SqlSession session = databaseSessionFactory.getSession().openSession();
        GoodsHistoryPriceMapper goodsHistoryPriceMapper = session.getMapper(GoodsHistoryPriceMapper.class);
        try {

            List<GoodsHistoryPriceModel> goodsHistoryPriceList = goodsHistoryPriceMapper.getHistoryPriceById(goodId);
            List<GraphicRowItemDTO> graphicRowItemList = new ArrayList<>();
            GraphicRowItemDTO row = new GraphicRowItemDTO();
            for (GoodsHistoryPriceModel model : goodsHistoryPriceList) {
                row.setDate(model.getCreatedAt());
                row.setPrice(model.getPrice());
                graphicRowItemList.add(row);
            }
            GraphicPriceDTO graphicPriceDTO = new GraphicPriceDTO();
            GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
            GoodsInfoModel goodsInfoModel = goodsInfoMapper.getGoodById(goodId);
            graphicPriceDTO.setTitle(goodsInfoModel.getTitle());
            graphicPriceDTO.setItems(graphicRowItemList);

        } catch (NullPointerException e) {
            System.out.println("Good with id = " + goodId + " not exist");
        }

    }


}
