package com.memecorps.memedex.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@Document
public class MemePost {
    @Id
    String id;

    @Size(min = 3, max = 30, message = "Username needs to be between 3 and 30 characters")
    String user;

    @NotBlank(message = "URL cannot be blank")
    @URL(message = "Please put a valid URL")
    String memeUrl;

    long timestamp;
}
