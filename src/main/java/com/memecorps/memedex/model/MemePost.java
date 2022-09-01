package com.memecorps.memedex.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class MemePost {
    @Id
    String id;

    String user;

    String memeUrl;

    long timestamp;
}
