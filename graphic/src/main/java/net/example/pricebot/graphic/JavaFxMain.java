package net.example.pricebot.graphic;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import net.example.pricebot.graphic.dto.GraphicPriceDTO;
import net.example.pricebot.graphic.dto.GraphicRowItemDTO;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class JavaFxMain extends Application {


    @Override
    public void start(Stage stage) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        LineChart lineChart = new LineChart<>(x, y);
        XYChart.Series series = new XYChart.Series();
        GraphicPriceDTO graphicPriceDTO = fullGraphicPriceDTO();
        GraphicRowItemDTO row = graphicPriceDTO.getItems().get(0);
        String START_DATE = String.valueOf(row.getDate());
        row = graphicPriceDTO.getItems().get(5);
        String FINISH_DATE = String.valueOf(row.getDate());
        y.setLabel("Price");
        x.setLabel("Dates");
        lineChart.setTitle(graphicPriceDTO.getTitle());
        series.setName("price from the " + START_DATE + " to the " + FINISH_DATE);

        for (GraphicRowItemDTO o : graphicPriceDTO.getItems()) {
            series.getData().add(new XYChart.Data<String, Integer>(o.getDate().toString(), o.getPrice()));
        }

        Scene scene = new Scene(lineChart, 600, 600);
        lineChart.getData().add(series);
        stage.setScene(scene);


        stage.show();
        createImage(lineChart);

    }

    public static void main(String[] args) {
        launch(args);


    }


    private void createImage(LineChart<Number, Number> numberLineChart) {
        WritableImage image = numberLineChart.snapshot(new SnapshotParameters(), null);
        String path = "C:\\Develop\\pricebot\\graphic\\src\\main\\java\\net\\example\\pricebot\\graphic\\Chart.png";
        File file = new File(path);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static LocalDateTime generateRandomDate() {
        int year = 2020;
        int month = (int) (Math.random() * (12 - 1)) + 1;
        int dayOfMonth = (int) (Math.random() * (28 - 1)) + 1;
        LocalDateTime date = LocalDateTime.of(year, month, dayOfMonth, 12, 12);
        return date;
    }


    private GraphicPriceDTO fullGraphicPriceDTO() {
        GraphicPriceDTO graphicPriceDTO = new GraphicPriceDTO();
        GraphicRowItemDTO row = null;

        Integer price = 4000;
        List<GraphicRowItemDTO> list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            row = new GraphicRowItemDTO(price, generateRandomDate());
            list.add(row);
            price = price + 1000;
        }

        list.sort(Comparator.comparing(GraphicRowItemDTO::getDate));
        graphicPriceDTO.setItems(list);
        graphicPriceDTO.setTitle("Title name");
        return graphicPriceDTO;
    }
}
