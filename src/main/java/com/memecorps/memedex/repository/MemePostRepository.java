package com.memecorps.memedex.repository;

import com.memecorps.memedex.model.MemePost;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemePostRepository extends MongoRepository<MemePost, String> {

}
