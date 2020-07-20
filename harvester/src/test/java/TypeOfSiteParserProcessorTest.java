import net.example.pricebot.harvester.exceptions.UnknownTypeOfSite;
import net.example.pricebot.harvester.parsers.TypeOfSiteParser;
import net.example.pricebot.harvester.typesofsites.AvitoTypeOfSite;
import net.example.pricebot.harvester.typesofsites.CommonTypeOfSite;
import org.junit.Assert;
import org.junit.Test;

public class TypeOfSiteParserProcessorTest {

    @Test
    public void avitoType() throws UnknownTypeOfSite {
        String url = "https://www.avito.ru/sankt-peterburg/noutbuki/noutbuk_asus_shustryy_videokarta_1gb_1926580826";
        AvitoTypeOfSite avitoType = new AvitoTypeOfSite(url);
        CommonTypeOfSite commonTypeOfSite = TypeOfSiteParser.parse(url);

        Assert.assertEquals(avitoType.getTypeOfSite(), commonTypeOfSite.getTypeOfSite());
    }


}
