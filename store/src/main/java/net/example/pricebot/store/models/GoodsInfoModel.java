package net.example.pricebot.store.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
public class GoodsInfoModel {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String telegramUserId;

    @Getter
    @Setter
    private String providerUrl;

    @Getter
    @Setter
    private String providerType;

    @Getter
    @Setter
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    private LocalDateTime createdAt;

    @Getter
    @Setter
    private Boolean isDeleted;

    public GoodsInfoModel(String telegramUserId, String providerUrl, String providerType, Boolean isDeleted, LocalDateTime updatedAt, LocalDateTime createdAt) {
        this.telegramUserId = telegramUserId;
        this.providerUrl = providerUrl;
        this.providerType = providerType;
        this.isDeleted = isDeleted;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public GoodsInfoModel(Long id, String telegramUserId, String providerUrl, String providerType, LocalDateTime updatedAt, LocalDateTime createdAt, Boolean isDeleted) {
        this.id = id;
        this.telegramUserId = telegramUserId;
        this.providerUrl = providerUrl;
        this.providerType = providerType;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "GoodsInfoModel: " +
                "id=" + id +
                ", telegramUserId='" + telegramUserId + '\'' +
                ", providerUrl='" + providerUrl + '\'' +
                ", providerType='" + providerType + '\'' +
                ", isDeleted=" + isDeleted +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt;
    }
}