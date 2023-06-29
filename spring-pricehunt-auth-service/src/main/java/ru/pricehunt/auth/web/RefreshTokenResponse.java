package ru.pricehunt.auth.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse {
    @JsonProperty("access")
    private String accessToken;
    @JsonProperty("refresh")
    private String refreshToken;
}
