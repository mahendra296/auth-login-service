package com.auth.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubEmail {
    private String email;
    private boolean primary;
    private boolean verified;
    private String visibility;
}
