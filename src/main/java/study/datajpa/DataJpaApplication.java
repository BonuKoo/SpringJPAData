package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;


@EnableJpaAuditing
@SpringBootApplication //이 어노테이션이 하위 패키지 레포지토리 싹 다 끌어다준다.
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}


	@Bean
	public AuditorAware<String> auditorProvider() {
		return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                return Optional.of(UUID.randomUUID().toString());
            }
        }; //이 람다식을 풀려면? -> Alt + Enter
	}
	//==return () -> Optional.of(UUID.randomUUID().toString());
	// 이 람다식을 풀려면? -> Alt + Enter

}
