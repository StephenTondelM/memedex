package com.memecorps.memedex.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class MemePost {
    @Id
    public String id;

    public String user;

    public String memeUrl;

    public String timestamp;
}
