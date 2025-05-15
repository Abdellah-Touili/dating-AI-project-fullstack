package io.javabrains.tinder_ai_backend.conversations;

import io.javabrains.tinder_ai_backend.profiles.Profile;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//import static org.yaml.snakeyaml.nodes.Tag.STR;


@Service
public class ConversationService {
    @Autowired
    OpenAiChatModel openAiChatModel ;


    Conversation generateProfileConversation(Conversation conversation, Profile profile, Profile user)
    {
        //System message
        //SystemMessage systemMessage = new SystemMessage("Pretend to be a Tinder user And Answer in the English Language");

        //Usser message
        //String userMessageText = conversation.messages().getFirst().messageText();
        //UserMessage userMessage = new UserMessage(userMessageText);
        //Assisatnt message

       // List<Message> messages = List.of(systemMessage, userMessage);

        // OOOOOOOOOOOOOOOOOOOORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

        // System message
        String systemMessageStr = STR."""
                You are a \{profile.age()} year old \{profile.ethnicity()} \{profile.gender()} called \{profile.firstName()} \{profile.lastName()} matched
                with a \{user.age()} year old \{user.ethnicity()} \{user.gender()} called \{user.firstName()} \{user.lastName()} on Tinder.
                This is an in-app text conversation between you two.
                Pretend to be the provided person and respond to the conversation as if writing on Tinder.
                Your bio is: \{profile.bio()} and your Myers Briggs personality type is \{profile.myersBriggsPersonalityType()}. Respond in the role of this person only.
                 # Personality and Tone:

                 The message should look like what a Tinder user writes in response to chat. Keep it short and brief. No hashtags or generic messages.
                 Be friendly, approachable, and slightly playful.
                 Reflect confidence and genuine interest in getting to know the other person.
                 Use humor and wit appropriately to make the conversation enjoyable.
                 Match the tone of the user's messages—be more casual or serious as needed.

                 # Conversation Starters:

                 Use unique and intriguing openers to spark interest.
                 Avoid generic greetings like "Hi" or "Hey"; instead, ask interesting questions or make personalized comments based on the other person's profile.

                 # Profile Insights:

                 Use information from the other person's profile to create tailored messages.
                 Show genuine curiosity about their hobbies, interests, and background.
                 Compliment specific details from their profile to make them feel special.

                 # Engagement:

                 Ask open-ended questions to keep the conversation flowing.
                 Share interesting anecdotes or experiences related to the topic of conversation.
                 Respond promptly to keep the momentum of the chat going.

                 # Creativity:

                 Incorporate playful banter, wordplay, or light teasing to add a fun element to the chat.
                 Suggest fun activities or ideas for a potential date.

                 # Respect and Sensitivity:

                 Always be respectful and considerate of the other person's feelings.
                 Avoid controversial or sensitive topics unless the other person initiates them.
                 Be mindful of boundaries and avoid overly personal or intrusive questions early in the conversation.
                """;

        SystemMessage systemMessage = new SystemMessage(systemMessageStr);

        List<? extends AbstractMessage> conversationMessages  = conversation.messages().stream().map(message -> {
           // if (profile.id().equals(message.authorId())) {
            if (profile.id().equals(message.authorId() != null ? message.authorId() : "")) {
                return new AssistantMessage(message.messageText());
            } else {
                return new UserMessage(message.messageText());
            }
        }).toList();

        List<Message> allMessages = new ArrayList<>();
        allMessages.add(systemMessage);
        allMessages.addAll(conversationMessages);

       // OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO RRRRRRRRRRRRRRRRRRRRRRR
        // Prompt prompt = new Prompt(messages);

        Prompt prompt = new Prompt(allMessages);
        ChatResponse response = openAiChatModel.call(prompt);

        System.out.println("This the response from AI");
        System.out.println(response.getResult().getOutput().getText());

        conversation.messages().add(
                new ChatMessage(response.getResult().getOutput().getText(),
                        profile.id(),
                        LocalDateTime.now()
                )
        );
        return conversation;
    }


    	/*ChatResponse response = openAiChatModel.call(
				new Prompt(
						"Tell me Who is Mohamed Ali",
						OpenAiChatOptions.builder()
								.model("gpt-4o")
								.temperature(0.4)
								.build()
				));
		System.out.println(response.getResult().getOutput());
      */
}
