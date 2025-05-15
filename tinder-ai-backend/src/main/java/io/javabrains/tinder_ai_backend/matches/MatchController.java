package io.javabrains.tinder_ai_backend.matches;

import io.javabrains.tinder_ai_backend.conversations.Conversation;
import io.javabrains.tinder_ai_backend.conversations.ConversationRepository;
import io.javabrains.tinder_ai_backend.conversations.CreateConversationRequest;
import io.javabrains.tinder_ai_backend.profiles.Profile;
import io.javabrains.tinder_ai_backend.profiles.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class MatchController {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MatchRepository matchRepository;


    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/matches")
    public Match createNewMatch(@RequestBody CreateMatchRequest request)
    {
        Profile profile = profileRepository.findById(request.profileId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Conversation conversation = new Conversation(
                UUID.randomUUID().toString(),
                profile.id(),
                new ArrayList<>()
        );
        Match match = new Match(UUID.randomUUID().toString(), profile, conversation.id());

        conversationRepository.save(conversation);
        matchRepository.save(match);

        return match;
    }
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/matches")
    public List<Match> getAllMatches()
    {
        return matchRepository.findAll();
    }
}
