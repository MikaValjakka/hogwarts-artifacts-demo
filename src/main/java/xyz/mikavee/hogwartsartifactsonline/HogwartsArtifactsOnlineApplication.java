package xyz.mikavee.hogwartsartifactsonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import xyz.mikavee.hogwartsartifactsonline.artifact.utils.IdWorker;

@SpringBootApplication
public class HogwartsArtifactsOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(HogwartsArtifactsOnlineApplication.class, args);
	}

	// Loading the twitter's snowflake id randomizer as bean
	@Bean
	public IdWorker idWorker() {
		return new IdWorker(1, 1);
	}

}
