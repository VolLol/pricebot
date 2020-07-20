package net.example.pricebot.harvester.typesofsites;

public class BeruTypeOfSite implements CommonTypeOfSite {

    private String url;

    public BeruTypeOfSite(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public TypeOfSite getTypeOfSite() {
        return TypeOfSite.YULA;
    }
}
