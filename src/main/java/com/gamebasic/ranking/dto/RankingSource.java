package com.gamebasic.ranking.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RankingSource {
    // JSON 랭킹 기록에서 수신을 받아 Service에 전달하기
    private Meta meta;
    private List<GameRecord> records;

    @Getter
    public static class Meta{
        private Season season;
    }

    @Getter
    public static class Season{
        private String id;
    }

    @Getter
    public static class GameRecord{
        private Long id;
        private Player player;
        private Run run;
        private BossFight bossFight;
        private Deck deck;
    }

    @Getter
    public static class Player{
        private String id;
        private String name;
    }

    @Getter
    public static class Run{
        private String status;
        private int clearedFloor;
        private int durationSeconds;
        private int finalHp;
    }

    @Getter
    public static class BossFight{
        private int totalTurns;
        private String finishingCard;
        private List<Phase> phases;
    }

    @Getter
    public static class Phase{
        private String phase;
        private int turns;
    }

    @Getter
    public static class Deck{
        private int size;
        private List<Card> cards;
    }

    @Getter
    public static class Card{
        private String cardType;
        private int acquiredFloor;
    }
}

