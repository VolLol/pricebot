package net.example.pricebot.core.usecases;

import javafx.stage.Stage;
import net.example.pricebot.core.dto.CreateImageAnswer;
import net.example.pricebot.graphic.ChartTool;
import net.example.pricebot.graphic.dto.ChartPriceDTO;
import net.example.pricebot.graphic.dto.ChartRowItemDTO;
import net.example.pricebot.store.mappers.GoodsHistoryPriceMapper;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.records.GoodsHistoryPriceRecord;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CreateImageUsecase {
    private static final Logger logger = LoggerFactory.getLogger(CreateImageUsecase.class);
    private static ChartPriceDTO chartPriceDTO;
    private static File image;

    public static CreateImageAnswer execute(SqlSession session, Long goodId, Stage stage) {
        CreateImageAnswer answer = new CreateImageAnswer();
        preparingDate(session, goodId);
        try {
            image = drawChart(stage);
            answer.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        answer.setImage(image);
        return answer;
    }

    private static void preparingDate(SqlSession session, Long goodId) {
        GoodsHistoryPriceMapper goodsHistoryPriceMapper = session.getMapper(GoodsHistoryPriceMapper.class);
        try {
            List<GoodsHistoryPriceRecord> goodsHistoryPriceList = goodsHistoryPriceMapper.searchTop14ByGoodsId(goodId);
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
            GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
            GoodsInfoRecord goodsInfoRecord = goodsInfoMapper.getById(goodId);

            chartPriceDTO = ChartPriceDTO.builder()
                    .title(goodsInfoRecord.getTitle())
                    .items(graphicRowItemList)
                    .startAt(LocalDateTime.now().minusDays(2L))
                    .finishAt(LocalDateTime.now())
                    .build();
            logger.info("Good with id = " + goodId + " exist. Starting to generate an image");
        } catch (NullPointerException e) {
            logger.info("Good with id = " + goodId + " not exist");
        }
    }

    private static File drawChart(Stage stage) throws IOException {

        File image = ChartTool.draw(stage, chartPriceDTO);
        logger.info(image.getAbsolutePath());
        return image;
    }
}
