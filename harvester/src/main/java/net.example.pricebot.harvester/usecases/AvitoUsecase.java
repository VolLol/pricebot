package net.example.pricebot.harvester.usecases;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AvitoUsecase {

    public List<Object> execute(String url) throws IOException {
        ArrayList<Object> answer = new ArrayList();
        Document document = Jsoup.connect(url).get();
        answer.add(getTitle(document));
        answer.add(getPrice(document));
        answer.add(getSellerName(document));
        answer.add(getDate(document));


        return answer;
    }

    private String getTitle(Document document) {
        Elements elementTitle = document.select("span.title-info-title-text");
        byte[] bytesTitle = elementTitle.text().getBytes(StandardCharsets.UTF_8);
        String stringTitle = new String(bytesTitle);
        return stringTitle;
    }

    private Integer getPrice(Document document) {
        Elements elementsPrice = document.select("span.js-item-price");
        byte[] bytesPrice = elementsPrice.get(0).text().getBytes(StandardCharsets.UTF_8);
        String stringPrice = new String(bytesPrice);
        stringPrice = stringPrice.replace(" ", "");
        Integer price = Integer.valueOf(stringPrice);
        return price;
    }

    private String getSellerName(Document document) {
        Elements elementsSellerName = document.select("div.seller-info-name");
        byte[] bytesSellerName = elementsSellerName.text().getBytes(StandardCharsets.UTF_8);
        return new String(bytesSellerName);
    }

    private String getDate(Document document) {
        Elements elementsDate = document.select("div.title-info-metadata-item-redesign");
        byte[] bytesDate = elementsDate.text().getBytes(StandardCharsets.UTF_8);
        String stringDate = new String(bytesDate);
        System.out.println(stringDate);
        return stringDate;
    }
}
