package net.example.pricebot.core.usecases;

import net.example.pricebot.core.dto.AddRecordAnswerEntity;
import net.example.pricebot.core.dto.CommonAnswerEntity;
import net.example.pricebot.core.dto.AnswerEnum;
import net.example.pricebot.harvester.HarvesterAvito;
import net.example.pricebot.harvester.dto.GoodsInfoDTO;
import net.example.pricebot.store.mappers.GoodsHistoryPriceMapper;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import net.example.pricebot.store.records.GoodsHistoryPriceRecord;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddRecordUsecase {
    private static final Logger logger = LoggerFactory.getLogger(AddRecordUsecase.class);

    public static CommonAnswerEntity execute(SqlSession session, Long telegramUserId, String url)  {
        logger.info("Start execute add record usecase");
        Pattern linkPattern = Pattern.compile("^https\\:\\/\\/www\\.avito\\.ru\\/.+");
        CommonAnswerEntity answer = new AddRecordAnswerEntity();
        Matcher matcher = linkPattern.matcher(url);
        if (matcher.matches()) {
            try {
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
                goodsInfoMapper.create(goodsInfoRecord);

                Long goodId = goodsInfoMapper.searchGoodByUrl(url);
                GoodsHistoryPriceRecord goodsHistoryPriceRecord = GoodsHistoryPriceRecord.builder()
                        .goodsInfoId(goodId)
                        .price(goodsInfoRecord.getPrice())
                        .createdAt(LocalDateTime.now())
                        .build();
                GoodsHistoryPriceMapper goodsHistoryPriceMapper = session.getMapper(GoodsHistoryPriceMapper.class);
                goodsHistoryPriceMapper.create(goodsHistoryPriceRecord);

                logger.info("Adding record successfully");
                answer.setMessageForUser("This good add to the watchlist");
                answer.setAnswerEnum(AnswerEnum.SUCCESSFUL);

            } catch (Exception e) {
                logger.info("This goods has already been added to the watchlist");
                answer.setAnswerEnum(AnswerEnum.UNSUCCESSFUL);
                answer.setMessageForUser("This goods is already on watchlist");
            }
        } else {
            logger.info("The user used an incorrect link");
            answer.setMessageForUser("Can't use this link. Please write another ");
            answer.setAnswerEnum(AnswerEnum.UNSUCCESSFUL);
        }
        return answer;

    }
}
