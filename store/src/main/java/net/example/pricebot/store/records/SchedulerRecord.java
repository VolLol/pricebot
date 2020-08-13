package net.example.pricebot.store.records;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@ToString
public class SchedulerRecord {

    @Getter
    @Setter
    private LocalDateTime executeDate;
}
