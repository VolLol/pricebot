package net.example.pricebot.core.dto;


import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnswerDto {

    @Getter
    @Setter
    private AnswerEnum answerEnum;


}
