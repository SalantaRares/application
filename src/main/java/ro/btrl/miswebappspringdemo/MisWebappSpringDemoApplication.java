package ro.btrl.miswebappspringdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class MisWebappSpringDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MisWebappSpringDemoApplication.class, args);
	}

}
