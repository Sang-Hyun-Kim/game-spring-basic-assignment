package com.gamebasic.runcard.repository;

import com.gamebasic.game.entity.Game;
import com.gamebasic.runcard.dto.DeckCount;
import com.gamebasic.runcard.entity.RunCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RunCardRepository extends JpaRepository<RunCard, Long> {
    List<RunCard> findAllByGameOrderByIdAsc(Game game);

    void deleteAllByGame(Game game);

    @Query("SELECT new com.gamebasic.runcard.dto.DeckCount(r.game.id, count(r.id)) " +
            "FROM RunCard r WHERE r.game IN (:games) " +
            "GROUP BY r.game.id")
    List<DeckCount> countByGames(@Param("games") List<Game> games);
}
// 1회 조회로 Games를 찾은 상태에서 이 Games 리스트를 그대로 사용해서 card를 그룹 +count하기
// select r.game_id, r.count(*) from run_cards r where :games Left Join r.game_id group by game_id order by game_id;
// count(*), count(alias)
// 위의 쿼리를 쓰려했으나 기존에 만든 함수 시그니처를 변경하고 싶지 않았음 따라서 해당 List<DeckCount> 반환값으로 할 것.