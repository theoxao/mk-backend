package com.theoxao.account.repository;

import com.theoxao.account.Message;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveCrudRepository<Message, ObjectId> {

    public Flux<Message> findByUserIdAndIdLessThanAndDelete(String userId, ObjectId id, Boolean delete);
}
