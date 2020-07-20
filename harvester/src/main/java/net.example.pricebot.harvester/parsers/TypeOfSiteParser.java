package net.example.pricebot.harvester.parsers;


import net.example.pricebot.harvester.exceptions.UnknownTypeOfSite;
import net.example.pricebot.harvester.typesofsites.AvitoTypeOfSite;
import net.example.pricebot.harvester.typesofsites.BeruTypeOfSite;
import net.example.pricebot.harvester.typesofsites.CommonTypeOfSite;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeOfSiteParser {

    final private static Pattern avitoTypeParser = Pattern.compile("(\\S+\\.avito\\.ru\\S+)");
    final private static Pattern beruTypeParser = Pattern.compile("(\\S+(\\.|\\s*)beru\\.ru\\S+)");

    public static CommonTypeOfSite parse(String link) throws UnknownTypeOfSite {

        Matcher matcher = avitoTypeParser.matcher(link);
        if (matcher.matches()) {
            String url = matcher.group(1);
            return new AvitoTypeOfSite(url);
        }

        matcher = beruTypeParser.matcher(link);
        if (matcher.matches()){
            String url = matcher.group(1);
            return new BeruTypeOfSite(url);
        }

        throw new UnknownTypeOfSite();
    }
}
