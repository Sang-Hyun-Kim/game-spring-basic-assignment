package com.gamebasic.game.controller;

import com.gamebasic.common.dto.PageResponse;
import com.gamebasic.game.dto.*;
import com.gamebasic.game.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping("/games")
    public ResponseEntity<GameDetailResponse> createGame(@Valid @RequestBody CreateRequest request) {
        GameDetailResponse created = gameService.createGame(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/games/{gameId}/progress")
    public ResponseEntity<GameDetailResponse> updateProgress(
        @PathVariable Long gameId,
        @Valid @RequestBody ProgressRequest request
    ) {
        return ResponseEntity.ok(gameService.updateProgress(gameId, request));
    }

    @GetMapping("/games")
    public ResponseEntity<List<GameSummaryResponse>> getGames() {
        return ResponseEntity.ok(gameService.getGames());
    }

    @GetMapping("/games/paged")
    public ResponseEntity<PageResponse<GameSummaryResponse>> getGamesPage(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        return ResponseEntity.ok(gameService.getGamesPage(pageable));
    }

    @GetMapping("/games/{gameId}")
    public ResponseEntity<GameDetailResponse> getGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }

    @PatchMapping("/games/{gameId}")
    public ResponseEntity<Void> renameGame(
            @PathVariable Long gameId,
            @Valid @RequestBody RenameRequest request
    ){
        gameService.renameGame(gameId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }

}
