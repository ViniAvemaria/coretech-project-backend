package com.vinicius.coretech.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class PhotoCredit {
    private String authorName;
    private String url;
    private String source;
}
