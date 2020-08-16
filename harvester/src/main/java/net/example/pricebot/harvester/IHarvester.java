package net.example.pricebot.harvester;

import net.example.pricebot.harvester.dto.GoodsInfoDTO;

import java.io.IOException;

interface IHarvester {
    GoodsInfoDTO getGoodsInfoByUrl(String url) throws IOException;

}
