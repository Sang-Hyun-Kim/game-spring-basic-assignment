package com.gamebasic.ranking.service;

import com.gamebasic.ranking.client.RankingClient;
import com.gamebasic.ranking.dto.GameRecordResponse;
import com.gamebasic.ranking.dto.RankingResponse;
import com.gamebasic.ranking.dto.RankingSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingClient rankingClient;

    public RankingResponse getRankings() {
        RankingSource rankingSource = rankingClient.fetch();

        // RankingSource의 다중 클래스를 분해해서 RankingResource로 재조립하기
        List<RankingSource.GameRecord> gameRecords = rankingSource.getRecords();
        RankingValidation rankingValidation = new RankingValidation(gameRecords);
        List<RankingSource.GameRecord> targetGameRecords = rankingValidation.returnValidTargetRecords();
        List<RankingSource.GameRecord> validGameRecords =
                rankingValidation.returnSortedRecords(rankingValidation.returnValidRecords(targetGameRecords));

        int excludedCount = targetGameRecords.size() - validGameRecords.size();
        List<GameRecordResponse> gameRecordResponses = new ArrayList<>();
        int rank = 1;
        HashSet<String> seenPlayerIds = new HashSet<String>();
        for(RankingSource.GameRecord record : validGameRecords){
            if(seenPlayerIds.add(record.getPlayer().getName())){ // java의 set은 넣을 때 중복이면 false를 반환해주는구나
                gameRecordResponses.add(
                        new GameRecordResponse(
                                rank++,
                                record.getPlayer().getName(),
                                record.getRun().getDurationSeconds(),
                                record.getRun().getFinalHp(),
                                record.getBossFight().getTotalTurns(),
                                record.getDeck().getSize()
                        )
                );
            }

        }
        return new RankingResponse(
                rankingSource.getMeta().getSeason().getId(),
                rankingSource.getRecords().size(),
                excludedCount,// 테스트용 설정
                gameRecordResponses//테스트용 설정
        );
    }


}
