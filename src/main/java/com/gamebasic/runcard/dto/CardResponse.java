package com.gamebasic.runcard.dto;

import lombok.Getter;

@Getter
public class CardResponse {
    // GameDetailResponse.java 참고 후 해결
    private final Long id;
    private final String cardType;
    private final int acquiredFloor;

    public CardResponse(Long id, String cardType, int acquiredFloor) {
        this.id = id;
        this.cardType = cardType;
        this.acquiredFloor = acquiredFloor;
    }
}
