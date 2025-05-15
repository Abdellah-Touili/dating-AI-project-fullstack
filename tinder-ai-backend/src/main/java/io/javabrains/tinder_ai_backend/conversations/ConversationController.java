package io.javabrains.tinder_ai_backend.conversations;

import io.javabrains.tinder_ai_backend.profiles.Profile;
import io.javabrains.tinder_ai_backend.profiles.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@RestController
public class ConversationController {
    @Autowired
    ConversationRepository conversationRepository;
    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ConversationService conversationService;

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/conversations/{conversationId}")
    public Conversation getConversation(@PathVariable String conversationId)
    {
        return conversationRepository.findById(conversationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation with Id: " + conversationId + " not found"));
    }
    @PostMapping("/conversations")
    public Conversation createNewConversation(@RequestBody CreateConversationRequest request)
    {
        profileRepository.findById(request.profileId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Conversation conversation = new Conversation(
                UUID.randomUUID().toString(),
                request.profileId(),
                new ArrayList<>()
        );

        conversationRepository.save(conversation);
        return conversation;
    }
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/conversations/addMsg/{conversationId}")
    public Conversation addMessageToConversation(@PathVariable String conversationId, @RequestBody ChatMessage chatMessage)
    {
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation with Id: " + conversationId + " not found"));

        String matchProfileId = conversation.profileId();
        Profile profile = profileRepository.findById(matchProfileId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unable to find a profile with ID " + matchProfileId
                ));

        System.out.println(profile);

        System.out.println("Author est :" +chatMessage.authorId());

       Profile user = profileRepository.findById(chatMessage.authorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unable to find a profile/User with ID " + chatMessage.authorId()
                ));

        // Create a new chat message and add it to the conversation
        //LocalDateTime.now()

        ChatMessage newchatMessage = new ChatMessage(chatMessage.messageText(),chatMessage.authorId(), LocalDateTime.now());
        conversation.messages().add(newchatMessage);
        conversationService.generateProfileConversation(conversation, profile, user);

        conversationRepository.save(conversation);
        return conversation;
    }
}
