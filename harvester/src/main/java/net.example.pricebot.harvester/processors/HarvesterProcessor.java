package net.example.pricebot.harvester.processors;


import net.example.pricebot.harvester.exceptions.UnknownTypeOfSite;
import net.example.pricebot.harvester.parsers.TypeOfSiteParser;
import net.example.pricebot.harvester.typesofsites.AvitoTypeOfSite;
import net.example.pricebot.harvester.typesofsites.BeruTypeOfSite;
import net.example.pricebot.harvester.typesofsites.CommonTypeOfSite;
import net.example.pricebot.harvester.usecases.AvitoUsecase;
import net.example.pricebot.harvester.usecases.BeruUsecase;

import java.io.IOException;
import java.util.ArrayList;

public class HarvesterProcessor {

    private AvitoUsecase avitoUsecase;
    private BeruUsecase beruUsecase;

    public HarvesterProcessor() {
        this.avitoUsecase = new AvitoUsecase();
        this.beruUsecase = new BeruUsecase();
    }


    public ArrayList execute(String url) {
        ArrayList<Object> outBuffer = new ArrayList<>();
        try {
            CommonTypeOfSite type = TypeOfSiteParser.parse(url);
            if (type instanceof AvitoTypeOfSite) {
                outBuffer.addAll(avitoUsecase.execute(type.getUrl()));
            }

            if (type instanceof BeruTypeOfSite) {
                outBuffer.addAll(beruUsecase.execute(type.getUrl()));
            }
            return outBuffer;
        } catch (UnknownTypeOfSite e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outBuffer;
    }

}





