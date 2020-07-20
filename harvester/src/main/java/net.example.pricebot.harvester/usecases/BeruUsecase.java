package net.example.pricebot.harvester.usecases;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BeruUsecase {


    public List<Object> execute(String url) {
        ArrayList<Object> answer = new ArrayList();
        try {
            Document document = Jsoup.connect(url).get();
            answer.add(getTitle(document));

            answer.add(getDate());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return answer;
    }

    private String getTitle(Document document) {
        Elements elementTitle = document.select("_1pTV0mQZJz _37FeBjfnZk _1eyJD_sk8K _brandTheme_default");
        byte[] bytesTitle = elementTitle.text().getBytes(StandardCharsets.UTF_8);
        String stringTitle = new String(bytesTitle);
        return stringTitle;
    }


    private LocalDate getDate() {
        LocalDate localDate = LocalDate.now();
        return localDate;
    }
}
