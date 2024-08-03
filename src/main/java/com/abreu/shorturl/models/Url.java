package com.abreu.shorturl.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "tb_urls")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Url {

    @Id
    private String id;

    private String url;

    @Indexed(expireAfterSeconds = 0)
    private LocalDateTime expiresAt;
}
