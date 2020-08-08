package net.example.pricebot.core.usecases;

import net.example.pricebot.core.dto.AddRecordAnswerEntity;
import net.example.pricebot.core.dto.CommonAnswerEntity;
import net.example.pricebot.core.dto.AnswerEnum;
import net.example.pricebot.harvester.HarvesterAvito;
import net.example.pricebot.harvester.dto.GoodsInfoDTO;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

public class AddRecordUsecase {
    private static final Logger logger = LoggerFactory.getLogger(AddRecordUsecase.class);

    public static CommonAnswerEntity execute(SqlSession session, Long telegramUserId, String url) throws IOException {
        CommonAnswerEntity answer = new AddRecordAnswerEntity();
        HarvesterAvito harvesterAvito = new HarvesterAvito();
        GoodsInfoDTO goodsInfoDTO = harvesterAvito.getGoodsInfoByUrl(url);
        GoodsInfoRecord goodsInfoRecord = GoodsInfoRecord.builder()
                .telegramUserId(telegramUserId.toString())
                .title(goodsInfoDTO.getTitle())
                .price(goodsInfoDTO.getPrice())
                .providerType("AVITO")
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(goodsInfoDTO.getUpdateAt())
                .providerUrl(url)
                .build();
        GoodsInfoMapper goodsInfoMapper = session.getMapper(GoodsInfoMapper.class);
        try {
            goodsInfoMapper.create(goodsInfoRecord);
            logger.info("Adding record successfully: " + goodsInfoRecord.toString());
            answer.setMessageForUser("This good add to the watchlist");
            answer.setAnswerEnum(AnswerEnum.SUCCESSFUL);
            return answer;
        } catch (Exception e) {
            logger.info("Error while adding record");
            answer.setAnswerEnum(AnswerEnum.UNSUCCESSFUL);
            answer.setMessageForUser("This goods is already on watchlist");
            return answer;
        } finally {
            session.commit();
        }
    }
}
