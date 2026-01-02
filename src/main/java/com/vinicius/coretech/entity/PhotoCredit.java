package com.vinicius.coretech.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PhotoCredit {
    private String authorName;
    private String url;
    private String source;

    public PhotoCredit(String authorName, String url, String source) {
        this.authorName = authorName;
        this.url = url;
        this.source = source;
    }
}
