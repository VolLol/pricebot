package net.example.pricebot.graphic;


import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import net.example.pricebot.graphic.dto.ChartPriceDTO;
import net.example.pricebot.graphic.dto.ChartRowItemDTO;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChartTool {

    private static final Object sync = new Object();
    private static final Object resultSync = new Object();
    private static volatile Boolean resultReady;
    private static volatile File resultFile;

    public static File draw(ChartPriceDTO chartPriceDTO) {
        synchronized (sync) {
            setResultReady(false);
            Platform.runLater(() -> {
                Stage stage = new Stage();
                CategoryAxis x = new CategoryAxis();
                NumberAxis y = new NumberAxis();
                LineChart lineChart = new LineChart<>(x, y);
                XYChart.Series series = new XYChart.Series();
                y.setLabel("Price");
                x.setLabel("Dates");
                lineChart.setTitle(chartPriceDTO.getTitle());
                series.setName("price from the " + chartPriceDTO.getStartAtAsStringDate() + " to the " + chartPriceDTO.getFinishAtAsStringDate());
                for (ChartRowItemDTO row : chartPriceDTO.getItems()) {
                    series.getData().add(new XYChart.Data<>(row.getDateAsString(), row.getPrice()));
                }

                Scene scene = new Scene(lineChart, 600, 600);
                lineChart.getData().add(series);
                stage.setScene(scene);
                stage.close();
                try {
                    setResultFile(saveChartToImage(lineChart));
                } catch (IOException e) {
                    setResultFile(null);
                }
                setResultReady(true);
            });

            while (!isResultReady()) {
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    break;
                }
            }

            return getResultFile();
        }
    }

    private static File saveChartToImage(LineChart<Number, Number> numberLineChart) throws IOException {
        WritableImage image = numberLineChart.snapshot(new SnapshotParameters(), null);
        Path tmpFile = Files.createTempDirectory("img");
        File file = new File(tmpFile.toString() + "/Chart.png");
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", file);
        return file;
    }

    private static void setResultFile(File file) {
        synchronized (resultSync) {
            resultFile = file;
        }
    }

    private static File getResultFile() {
        synchronized (resultSync) {
            return resultFile;
        }
    }

    private static void setResultReady(Boolean flag) {
        synchronized (resultSync) {
            resultReady = flag;
        }
    }

    private static Boolean isResultReady() {
        synchronized (resultSync) {
            return resultReady;
        }
    }
}