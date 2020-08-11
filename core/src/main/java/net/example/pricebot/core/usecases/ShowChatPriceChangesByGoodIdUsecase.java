package net.example.pricebot.core.usecases;

import net.example.pricebot.core.dto.DTOEnum;
import net.example.pricebot.core.dto.ShowChatPriceChangesByGoodIdDTO;
import net.example.pricebot.graphic.ChartTool;
import net.example.pricebot.graphic.dto.ChartPriceDTO;
import net.example.pricebot.graphic.dto.ChartRowItemDTO;
import net.example.pricebot.store.mappers.GoodsHistoryPriceMapper;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.records.GoodsHistoryPriceRecord;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShowChatPriceChangesByGoodIdUsecase {
    private static final Logger logger = LoggerFactory.getLogger(ShowChatPriceChangesByGoodIdUsecase.class);
    private final SqlSessionFactory sqlSessionFactory;


    public ShowChatPriceChangesByGoodIdUsecase(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public ShowChatPriceChangesByGoodIdDTO execute(Long goodId) {
        logger.info("Start execute show diagram usecase");
        ShowChatPriceChangesByGoodIdDTO answer = new ShowChatPriceChangesByGoodIdDTO();
        try {
            ChartPriceDTO chartPriceDTO = preparingDate(goodId);
            File image = ChartTool.draw(chartPriceDTO);
            answer.setImage(image);
            answer.setDTOEnum(DTOEnum.SUCCESSFUL);
            answer.setMessageForUser("It is diagram for good with id " + goodId);
            logger.info("Successful execution diagram usecase");
        } catch (NullPointerException e) {
            logger.info("Goods with id = " + goodId + " not exist");
            answer.setDTOEnum(DTOEnum.UNSUCCESSFUL);
            answer.setMessageForUser("Goods with id = " + goodId + " not exist");
        }
        return answer;
    }

    private ChartPriceDTO preparingDate(Long goodId) {
        SqlSession session = sqlSessionFactory.openSession();
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
