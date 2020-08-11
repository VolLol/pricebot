package net.example.pricebot.core.dto;


import lombok.Getter;
import lombok.Setter;

public class CommonDTO {

    @Getter
    @Setter
    DTOEnum DTOEnum;

    @Setter
    @Getter
    String messageForUser;
}
