package io.javabrains.tinder_ai_backend.matches;

import io.javabrains.tinder_ai_backend.conversations.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.UUID;


public interface MatchRepository extends MongoRepository<Match, String> {
}
