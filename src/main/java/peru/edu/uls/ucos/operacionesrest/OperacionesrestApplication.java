package peru.edu.uls.ucos.operacionesrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class OperacionesrestApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(OperacionesrestApplication.class);
		application.setDefaultProperties(Map.of(
				"spring.datasource.url", "jdbc:postgresql://ep-broad-hall-b461p1rk-pooler.c-6.us-east-2.aws.neon.tech/neondb?sslmode=require",
				"spring.datasource.username", "neondb_owner",
				"spring.datasource.driver-class-name", "org.postgresql.Driver",
				"spring.jpa.hibernate.ddl-auto", "update",
				"spring.jpa.show-sql", "true"
		));
		application.run(args);
	}

}
