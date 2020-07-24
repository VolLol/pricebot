package net.example.pricebot.harvester;

import net.example.pricebot.harvester.dto.GoodsInfoDTO;
import net.example.pricebot.harvester.dto.GoodsInfoProvider;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        int year = Year.now().getValue();
        int month = 10;
        int day = 0;
        int hour = 0;
        int minute = 0;
        Pattern partlyDatePattern = Pattern.compile("^\\D+(\\d+)\\:(\\d+)");
        if (stringDate.contains("сегодня")) {
            month = LocalDateTime.now().getMonthValue();
            day = LocalDateTime.now().getDayOfMonth();
            Matcher matcher = partlyDatePattern.matcher(stringDate);
            if (matcher.matches()) {
                String stringhour = matcher.group(1);
                hour = Integer.parseInt(stringhour);
                String stringMinute = matcher.group(2);
                minute = Integer.parseInt(stringMinute);
            }
        }
        if (stringDate.contains("вчера")) {
            month = LocalDateTime.now().getMonthValue();
            day = LocalDateTime.now().getDayOfMonth() - 1;
            Matcher matcher = partlyDatePattern.matcher(stringDate);
            if (matcher.matches()) {
                String stringHour = matcher.group(1);
                hour = Integer.parseInt(stringHour);
                String stringMinute = matcher.group(2);
                minute = Integer.parseInt(stringMinute);
            }
        }

        if (!(stringDate.contains("сегодня")) && !stringDate.contains("вчера")) {
            List<String> listDate = Arrays.asList(stringDate.split(" "));
            String stringDay = listDate.get(0);
            day = Integer.parseInt(stringDay);
            String stringMonth = listDate.get(1);
            month = determineTheMonth(stringMonth);
            String time = listDate.get(3);
            List<String> listTime = Arrays.asList(time.split(":"));
            hour = Integer.parseInt(listTime.get(0));
            minute = Integer.parseInt(listTime.get(1));

        }


        return LocalDateTime.of(year, month, day, hour, minute);

    }

    private int determineTheMonth(String monthName) {
        int month = 1;
        switch (monthName) {
            case "января":
                month = 1;
                break;
            case "февраля":
                month = 2;
                break;
            case "марта":
                month = 3;
                break;
            case "апреля":
                month = 4;
                break;
            case "мая":
                month = 5;
                break;
            case "июня":
                month = 6;
                break;
            case "июля":
                month = 7;
                break;
            case "августа":
                month = 8;
                break;
            case "сентября":
                month = 9;
                break;
            case "октября":
                month = 10;
                break;
            case "ноября":
                month = 11;
                break;
            case "декабря":
                month = 12;
                break;
        }
        return month;
    }
}
