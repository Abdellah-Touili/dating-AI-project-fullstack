package io.javabrains.tinder_ai_backend;

import io.javabrains.tinder_ai_backend.conversations.ConversationRepository;
import io.javabrains.tinder_ai_backend.profiles.Profile;
import io.javabrains.tinder_ai_backend.profiles.ProfileRepository;
import io.javabrains.tinder_ai_backend.profiles.service.ProfileCreationService;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

import static io.javabrains.tinder_ai_backend.profiles.Gender.FEMALE;
import static io.javabrains.tinder_ai_backend.profiles.Gender.MALE;

@SpringBootApplication
public class TinderAiBackendApplication implements CommandLineRunner {
	@Autowired
	ProfileRepository profileRepository;
	@Autowired
	ConversationRepository conversationRepository;
	@Autowired
	ProfileCreationService profileCreationService;
	@Autowired
	OpenAiChatModel openAiChatModel ;

	public static void main(String[] args){
		SpringApplication.run(TinderAiBackendApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {

		Profile profile1 = new Profile(
		"1",
		"Abdou",
		"Charife",
		50,
		"White",
		 MALE,
		"Ing Déveloper Full Stack",
		"myphoto.jpg",
		"INA"
		);
        Profile profile2 = new Profile(
				"2",
				"Halima",
				"saadi",
				20,
				"Black",
				FEMALE,
				"Marketing",
				"Herephoto.jpg",
				"INASA"
		);
		profileRepository.save(profile1);
		profileRepository.save(profile2);
		//profileRepository.findAll().forEach(System.out::println);

		//conversationRepository.findAll().forEach(System.out::println);

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
		profileCreationService.saveProfilesToDB();

	}
}
