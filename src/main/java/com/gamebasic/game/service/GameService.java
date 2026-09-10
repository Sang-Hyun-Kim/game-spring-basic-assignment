package com.gamebasic.game.service;

import com.gamebasic.common.exception.GameFinishedException;
import com.gamebasic.common.exception.GameNotFoundException;
import com.gamebasic.game.dto.*;
import com.gamebasic.game.entity.Game;
import com.gamebasic.game.repository.GameRepository;
import com.gamebasic.runcard.dto.CardResponse;
import com.gamebasic.runcard.dto.DeckCount;
import com.gamebasic.runcard.dto.RunCardRequest;
import com.gamebasic.runcard.entity.RunCard;
import com.gamebasic.runcard.repository.RunCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final RunCardRepository runCardRepository;

    @Transactional
    public GameDetailResponse createGame(CreateRequest request) {
        Game game = gameRepository.save(new Game(request.getPlayerName()));
        saveDeck(game, request.getDeck());

        return new GameDetailResponse(
            game.getId(),
            game.getPlayerName(),
            game.getCurrentHp(),
            game.getCurrentFloor(),
            game.getPhase(),
            game.getStatus(),
            ReturnDeck(game),
            game.getCreatedAt(),
            game.getUpdatedAt()
        );
    }

    private void saveDeck(Game game, List<RunCardRequest> deck) {
        List<RunCard> cards = new ArrayList<>();
        for (RunCardRequest card : deck) {
            cards.add(new RunCard(game, card.getCardType(), card.getAcquiredFloor()));
        }
        runCardRepository.saveAll(cards);
    }

    private Game findGame(Long gameId) {
        return gameRepository.findById(gameId)
            .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    @Transactional
    public GameDetailResponse updateProgress(Long gameId, ProgressRequest request) {
        Game game = findGame(gameId);
        if(game.isFinished())
        {
            throw new GameFinishedException(gameId);
        }

        game.updateProgress(
            request.getCurrentHp(),
            request.getCurrentFloor(),
            request.getPhase(),
            request.getStatus()
        );

        // 요청의 deck은 저장할 덱 전체이므로 기존 카드를 모두 지우고 요청 순서대로 다시 저장합니다.
        runCardRepository.deleteAllByGame(game);
        saveDeck(game, request.getDeck());

        return new GameDetailResponse(
            game.getId(),
            game.getPlayerName(),
            game.getCurrentHp(),
            game.getCurrentFloor(),
            game.getPhase(),
            game.getStatus(),
            ReturnDeck(game),
            game.getCreatedAt(),
            game.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<GameSummaryResponse> getGames() {
        List<Game> games = gameRepository.findAllByOrderByIdDesc();
        List<DeckCount> deckCounts = runCardRepository.countByGames(games);

        List<GameSummaryResponse> gameSummaryResponseList = new ArrayList<>();
        HashMap<Long,Long> gameIdDeckSizeMap = new HashMap<>();
        for(DeckCount deckCount : deckCounts) {
            gameIdDeckSizeMap.put(deckCount.getGameId(), deckCount.getDeckCount());
        }
        for(Game game : games) {
            GameSummaryResponse gameSummaryResponse = new GameSummaryResponse(
                    game.getId(),
                    game.getPlayerName(),
                    game.getCurrentHp(),
                    game.getCurrentFloor(),
                    game.getPhase(),
                    game.getStatus(),
                    gameIdDeckSizeMap.getOrDefault(game.getId(), 0L),
                    game.getCreatedAt(),
                    game.getUpdatedAt()
            );
            gameSummaryResponseList.add(gameSummaryResponse);

        }
        return gameSummaryResponseList;
    }

    @Transactional(readOnly = true)
    public GameDetailResponse getGame(Long gameId) {
        Game game = findGame(gameId);

        return new GameDetailResponse(
                game.getId(),
                game.getPlayerName(),
                game.getCurrentHp(),
                game.getCurrentFloor(),
                game.getPhase(),
                game.getStatus(),
                ReturnDeck(game),
                game.getCreatedAt(),
                game.getUpdatedAt()
        );
    }

    // 반복된 List<RunCard> 생성 부분을 함수화해서 deck은 반환하는 헬퍼 클래스 제작

    @Transactional(readOnly = true)
    protected List<CardResponse> ReturnDeck(Game game) {
        List<RunCard> cards = runCardRepository.findAllByGameOrderByIdAsc(game);
        List<CardResponse> deck = new ArrayList<>();
        for(RunCard card : cards) {
            deck.add(new CardResponse(card.getId(), card.getCardType(), card.getAcquiredFloor()));
        }
        return deck;
    }

    @Transactional
    public void renameGame(Long gameId, RenameRequest renameRequest) {
        Game game = findGame(gameId);
        game.rename(renameRequest.getPlayerName());
    }

    @Transactional
    public void deleteGame(Long gameId) {
        Game game = findGame(gameId); // 피드백: 기존 findGame 함수에서 없다면 HttpCode로 반환해줌, 따라서 예외처리 코드 삭제
        runCardRepository.deleteAllByGame(game); // 피드백 RunCard 삭제를 깜박함
        gameRepository.delete(game);
    }
}
