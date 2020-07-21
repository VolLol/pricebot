package net.example.pricebot.harvester;

import net.example.pricebot.harvester.dto.GoodsInfoDTO;
import net.example.pricebot.harvester.dto.GoodsInfoProvider;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class HarvesterAvito implements IHarvester {

    private Document webPage;

    @Override
    public GoodsInfoDTO getGoodsInfoByUrl(String url) throws IOException {
        webPage = Jsoup.connect(url).get();
        GoodsInfoDTO goodsInfoDTO = GoodsInfoDTO.builder()
                .title(getTitle())
                .price(getPrice())
                .updateAt(getUpdateAt())
                .provider(GoodsInfoProvider.AVITO)
                .build();
        return goodsInfoDTO;
    }


    private String getTitle() {
        Elements elementTitle = webPage.select("span.title-info-title-text");
        byte[] bytesTitle = elementTitle.text().getBytes(StandardCharsets.UTF_8);
        String stringTitle = new String(bytesTitle);
        return stringTitle;
    }

    private Integer getPrice() {
        Elements elementsPrice = webPage.select("span.js-item-price");
        byte[] bytesPrice = elementsPrice.get(0).text().getBytes(StandardCharsets.UTF_8);
        String stringPrice = new String(bytesPrice);
        stringPrice = stringPrice.replace(" ", "");
        Integer price = Integer.valueOf(stringPrice);
        return price;
    }

    private LocalDateTime getUpdateAt() {
        Elements elementsDate = webPage.select("div.title-info-metadata-item-redesign");
        byte[] bytesDate = elementsDate.text().getBytes(StandardCharsets.UTF_8);
        String stringDate = new String(bytesDate);
        System.out.println(stringDate);

        return LocalDateTime.now();
    }


}
