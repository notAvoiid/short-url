package com.abreu.shorturl.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlRequestDTO {

    @NotBlank(message = "A URL original é obrigatória")
    @Pattern(
        regexp = "^(https?|ftp)://([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:[0-9]+)?(/.*)?$",
        message = "URL inválida. Use o formato: http(s)://exemplo.com"
    )
    private String url;

    @Positive(message = "O tempo de expiração deve ser positivo")
    private Integer minutes = 3;

}
