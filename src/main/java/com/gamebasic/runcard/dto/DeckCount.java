package com.gamebasic.runcard.dto;

import lombok.Getter;

@Getter
public class DeckCount {
    private final Long gameId;
    private final Long deckCount;

    public DeckCount(Long gameId, Long deckCount){
            this.gameId = gameId;
            this.deckCount = deckCount;
    }
}
