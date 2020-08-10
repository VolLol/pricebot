package net.example.pricebot.core.usecases;

import net.example.pricebot.core.answerEntityes.AnswerEnum;
import net.example.pricebot.core.answerEntityes.CreateImageAnswerEntity;
import net.example.pricebot.graphic.ChartTool;
import net.example.pricebot.graphic.dto.ChartPriceDTO;
import net.example.pricebot.graphic.dto.ChartRowItemDTO;
import net.example.pricebot.store.DatabaseMigrationTools;
import net.example.pricebot.store.DatabaseSessionFactory;
import net.example.pricebot.store.mappers.GoodsHistoryPriceMapper;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.records.GoodsHistoryPriceRecord;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShowDiagramUsecase {
    private static final Logger logger = LoggerFactory.getLogger(ShowDiagramUsecase.class);
    private final String driver = "org.postgresql.Driver";
    private final String JDBCUrl = "jdbc:postgresql://localhost:5432/pricebotdb";
    private final String username = "postgres";
    private final String password = "password";
    private final SqlSession session;

    public ShowDiagramUsecase() {
        DatabaseMigrationTools.updateDatabaseVersion(JDBCUrl, username, password);
        PooledDataSource pooledDataSource = new PooledDataSource(driver, JDBCUrl, username, password);
        DatabaseSessionFactory databaseSessionFactory = new DatabaseSessionFactory(pooledDataSource);
        session = databaseSessionFactory.getSession().openSession();
    }

    public CreateImageAnswerEntity execute(Long goodId) {
        logger.info("Start execute show diagram usecase");
        CreateImageAnswerEntity answer = new CreateImageAnswerEntity();
        try {
            ChartPriceDTO chartPriceDTO = preparingDate(goodId);
            File image = ChartTool.draw(chartPriceDTO);
            answer.setImage(image);
            answer.setAnswerEnum(AnswerEnum.SUCCESSFUL);
            answer.setMessageForUser("It is diagram for good with id " + goodId);
            logger.info("Successful execution diagram usecase");
        } catch (NullPointerException e) {
            logger.info("Goods with id = " + goodId + " not exist");
            answer.setAnswerEnum(AnswerEnum.UNSUCCESSFUL);
            answer.setMessageForUser("Goods with id = " + goodId + " not exist");
        } finally {
            session.close();
        }
        return answer;
    }

    private ChartPriceDTO preparingDate(Long goodId) {
        GoodsHistoryPriceMapper goodsHistoryPriceMapper = session.getMapper(GoodsHistoryPriceMapper.class);
        List<GoodsHistoryPriceRecord> goodsHistoryPriceList = goodsHistoryPriceMapper.searchTop14ByGoodsId(goodId);
        if (!goodsHistoryPriceList.isEmpty()) {
            List<ChartRowItemDTO> graphicRowItemList = new ArrayList<>();
            ChartRowItemDTO row;
            for (GoodsHistoryPriceRecord model : goodsHistoryPriceList) {
                row = ChartRowItemDTO.builder()
                        .date(model.getCreatedAt())
                        .price(model.getPrice())
                        .build();
                graphicRowItemList.add(row);
            }
            graphicRowItemList.sort(Comparator.comparing(ChartRowItemDTO::getDate));
            LocalDateTime firstDate = graphicRowItemList.get(0).getDate();
            LocalDateTime lastDate = graphicRowItemList.get(graphicRowItemList.size() - 1).getDate();

            GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
            GoodsInfoRecord goodsInfoRecord = goodsInfoMapper.getById(goodId);

            ChartPriceDTO chartPriceDTO = ChartPriceDTO.builder()
                    .title(goodsInfoRecord.getTitle())
                    .items(graphicRowItemList)
                    .startAt(firstDate)
                    .finishAt(lastDate)
                    .build();
            logger.info("Finished preparing data goods with id = " + goodId + ". Starting to generate an image");
            session.close();
            return chartPriceDTO;
        } else {
            logger.info("Goods with id = " + goodId + " not exist");
            throw new NullPointerException();
        }
    }

}
