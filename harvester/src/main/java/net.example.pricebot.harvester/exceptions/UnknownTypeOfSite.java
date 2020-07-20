package net.example.pricebot.harvester.exceptions;

public class UnknownTypeOfSite extends Throwable {
    public UnknownTypeOfSite() {
    }

    public UnknownTypeOfSite(String message) {
        super(message);
    }
}
