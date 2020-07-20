import net.example.pricebot.harvester.usecases.AvitoUsecase;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class AvitoUsecaseTest {

    @Test
    public void correctLink() throws IOException {
        AvitoUsecase avitoUsecase = new AvitoUsecase();
        String url = "https://www.avito.ru/sankt-peterburg/noutbuki/noutbuk_asus_shustryy_videokarta_1gb_1926580826";
        List<Object> answer = avitoUsecase.execute(url);
        String name = "Ноутбук Asus Шустрый Видеокарта 1GB";
        String sellerName = "Нева-Комп Главный Компьютерный Магазин №1 в СПБ";
        String date = "Вчера в 23:17";

        Assert.assertEquals(4, answer.size());
        Assert.assertEquals(name, answer.get(0));
        Assert.assertEquals(6900, answer.get(1));
        Assert.assertEquals(sellerName, answer.get(2));
        Assert.assertEquals(date, answer.get(3));

    }
}
