package com.gamebasic.ranking.service;

import com.gamebasic.ranking.CardType;
import com.gamebasic.ranking.dto.RankingSource;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RankingValidation {
    private final List<RankingSource.GameRecord> gameRecords;

    private static final Set<String> cardTypes =
            Stream.of(CardType.values())
                    .map(CardType::name)
                    .collect(Collectors.toSet()); // 카드 열거형 타입들 검증용 컨테이너, 반복사용

    // 검증된 게임 기록을 요구사항 정렬 순서에 맞춰 정렬하는 비교 객체 -> static final 상수의 이름 컨벤션 피드백(대문자)
    private static final Comparator<RankingSource.GameRecord> RECORD_COMPARATOR =
            Comparator.comparingInt((RankingSource.GameRecord r) -> r.getRun().getDurationSeconds())
                    .thenComparing(r->r.getRun().getFinalHp(),Comparator.reverseOrder())
                    .thenComparing(RankingSource.GameRecord::getId);

    public RankingValidation(
        List<RankingSource.GameRecord> gameRecords
    ){
        this.gameRecords = List.copyOf(gameRecords);
    }

    public List<RankingSource.GameRecord> returnValidTargetRecords(){
        return gameRecords.stream().filter(this::isRankingTarget)
                .toList();
    }

    public List<RankingSource.GameRecord> returnValidRecords(List<RankingSource.GameRecord> targetGameRecord){
        return targetGameRecord.stream().filter(this::isValidRecord)
                .toList();
    }

    public List<RankingSource.GameRecord> returnSortedRecords(List<RankingSource.GameRecord> targetGameRecord){
        // C++ 처럼 함수 객체를 통한 비교를 람다로 처리하려고 이것저것 찾아봤음(난이도 높았다)
        return targetGameRecord.stream().sorted(RECORD_COMPARATOR).toList();
    }

    // 조건 파악을 위한 헬퍼 함수들 제작하기
    private boolean isRankingTarget(RankingSource.GameRecord gameRecord){
        RankingSource.Run run = gameRecord.getRun();
        return (run.getStatus().equals("CLEARED")) && (run.getClearedFloor() == 10);
    }

    private boolean isValidRecord(RankingSource.GameRecord gameRecord){
        return hasValidDuration(gameRecord.getRun())
                && hasValidHp(gameRecord.getRun())
                && hasValidDeckSize(gameRecord.getDeck())
                && hasValidCardType(gameRecord.getDeck())
                && hasValidAcquiredFloors(gameRecord.getDeck())
                && hasValidBossFight(gameRecord.getBossFight())
                && hasValidFinishingCard(gameRecord.getBossFight(),gameRecord.getDeck());
    }

    private boolean hasValidDuration(RankingSource.Run run){
        return run.getDurationSeconds() >= run.getClearedFloor() * 30;
    }

    private boolean hasValidHp(RankingSource.Run run){
        return (run.getFinalHp() <= 99)&& (run.getFinalHp() >= 1);
    }

    private boolean hasValidDeckSize(RankingSource.Deck deck){
        return (deck.getSize() >= 9)
                && (deck.getSize() <= 20)
                && (deck.getCards().size() == deck.getSize());
    }

    private boolean hasValidCardType(RankingSource.Deck deck){
        List<RankingSource.Card> cards = deck.getCards();
        return cards.stream().allMatch(c -> cardTypes.contains(c.getCardType()));
    }

    private boolean hasValidAcquiredFloors(RankingSource.Deck deck){
        List<RankingSource.Card> cards = deck.getCards();
        return cards.stream().allMatch(c -> c.getAcquiredFloor() >= 0 && c.getAcquiredFloor() <= 9);
    }

    private boolean hasValidBossFight(RankingSource.BossFight bossFight){
        if(bossFight == null || bossFight.getPhases() == null) return false;
        List<RankingSource.Phase> phases = bossFight.getPhases();

        // 자바 Stream으로 특정 필드만 추출하기
        List<String> phaseName = phases.stream().map(RankingSource.Phase::getPhase)
                .toList();
        if(!phaseName.equals(List.of("THRONE","UNBOUND","ECLIPSE"))) return false;
        // 해당 Java의 문자열 비교 함수는 크기는 물론 순서, 각 문자열까지 비교를 완료한 결과를 반환한다.

        int sumOfPhaseTurns = 0;
        for(RankingSource.Phase phase : phases){
            if(phase.getTurns() < 1) return false;
            sumOfPhaseTurns += phase.getTurns();
        }
        return bossFight.getTotalTurns() == sumOfPhaseTurns;
    }

    private boolean hasValidFinishingCard(RankingSource.BossFight bossFight
            , RankingSource.Deck deck){
        List<RankingSource.Card> cards = deck.getCards();
        return cards.stream().anyMatch(c -> bossFight.getFinishingCard().equals(c.getCardType()));
    }
}
