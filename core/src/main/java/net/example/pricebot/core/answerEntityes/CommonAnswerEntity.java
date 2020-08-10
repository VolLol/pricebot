package net.example.pricebot.core.answerEntityes;


import lombok.Getter;
import lombok.Setter;

public class CommonAnswerEntity {

    @Getter
    @Setter
    AnswerEnum answerEnum;

    @Setter
    @Getter
    String messageForUser;
}
