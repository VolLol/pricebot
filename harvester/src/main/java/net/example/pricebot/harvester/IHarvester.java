package net.example.pricebot.harvester;

import net.example.pricebot.harvester.dto.GoodsInfoDTO;

import java.io.IOException;

public interface IHarvester {
    public GoodsInfoDTO getGoodsInfoByUrl(String url) throws IOException;

}
