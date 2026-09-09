package com.gamebasic.game.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RenameRequest {
    @NotBlank
    @Size(min = 2, max = 12)
    private String playerName;//NewPlayerName이라고 명세에 안맞게 적고 필드명 불일치로 오류 발생
}
