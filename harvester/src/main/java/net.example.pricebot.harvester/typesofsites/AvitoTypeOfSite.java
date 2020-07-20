package net.example.pricebot.harvester.typesofsites;


public class AvitoTypeOfSite implements CommonTypeOfSite {

    private String url;

    public AvitoTypeOfSite(String url) {
        this.url = url;
    }


    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public TypeOfSite getTypeOfSite() {
        return TypeOfSite.AVITO;
    }
}
