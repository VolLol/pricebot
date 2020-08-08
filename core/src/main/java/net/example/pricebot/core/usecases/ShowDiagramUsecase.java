package net.example.pricebot.core.usecases;

import javafx.stage.Stage;
import net.example.pricebot.core.dto.AnswerEnum;
import net.example.pricebot.core.dto.CreateImageAnswerEntity;
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

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShowDiagramUsecase {
    private static final Logger logger = LoggerFactory.getLogger(ShowDiagramUsecase.class);
    private static ChartPriceDTO chartPriceDTO;
    private static File image;
    private static CreateImageAnswerEntity answer = new CreateImageAnswerEntity();

    public ShowDiagramUsecase() {
    }

    public static CreateImageAnswerEntity execute(SqlSession session, Stage stage, String goodId) {
        preparingDate(session, Long.valueOf(goodId));
        image = drawChart(stage);
        answer.setImage(image);
        answer.setAnswerEnum(AnswerEnum.SUCCESSFUL);
        answer.setMessageForUser("It is giagram for good with id " + goodId);

        return answer;
    }

    private static void preparingDate(SqlSession session, Long goodId) {
        GoodsHistoryPriceMapper goodsHistoryPriceMapper = session.getMapper(GoodsHistoryPriceMapper.class);
        try {
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

                chartPriceDTO = ChartPriceDTO.builder()
                        .title(goodsInfoRecord.getTitle())
                        .items(graphicRowItemList)
                        .startAt(firstDate)
                        .finishAt(lastDate)
                        .build();
                logger.info("Finished preparing data goods with id = " + goodId + ". Starting to generate an image");
            } else {
                answer.setMessageForUser("Not enough information for plotting");
            }
        } catch (NullPointerException e) {
            logger.info("Good with id = " + goodId + " not exist");
        }
    }

    private static File drawChart(Stage stage) {
        try {
            image = ChartTool.draw(stage, chartPriceDTO);
            Path tmpFile = Files.createTempDirectory("img");
            File file = new File(tmpFile.toString() + "/Chart.png");
            ImageIO.write((RenderedImage) image, "PNG", file);
            logger.info("Finished generating the image");
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}
