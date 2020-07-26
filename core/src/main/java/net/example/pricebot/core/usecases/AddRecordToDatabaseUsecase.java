package net.example.pricebot.core.usecases;

import net.example.pricebot.core.dto.AnswerDto;
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

public class AddRecordToDatabaseUsecase {
    private static final Logger logger = LoggerFactory.getLogger(AddRecordToDatabaseUsecase.class);

    public static AnswerDto execute(SqlSession session, String url) throws IOException {
        HarvesterAvito harvesterAvito = new HarvesterAvito();
        GoodsInfoDTO goodsInfoDTO = harvesterAvito.getGoodsInfoByUrl(url);
        GoodsInfoRecord goodsInfoRecord = GoodsInfoRecord.builder()
                .telegramUserId("telegram id")
                .title(goodsInfoDTO.getTitle())
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
            return AnswerDto.builder().answerEnum(AnswerEnum.SUCCESSFUL).build();

        } catch (Exception e) {
            logger.info("Error while adding record");
            return AnswerDto.builder().answerEnum(AnswerEnum.UNSUCCESSFUL).build();
        } finally {
            session.commit();
        }

    }
}
