package net.example.pricebot.harvester.entities;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommonEntity {

    @Setter
    private String title;

    @Setter
    private String price;

    @Setter
    private String date;

}
