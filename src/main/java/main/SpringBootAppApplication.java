package main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootAppApplication {
	@Autowired
	TimeCardRepository timeCardRepo;
	UserInfoRepository userInfoRepo;
	
	public static void main(String[] args) {		
		SpringApplication.run(SpringBootAppApplication.class, args);
	}

/*
	@PostConstruct
	public void initialData() {

		repository.save(new WorkingTimeEntity("aaa", "bbb"));
		repository.save(new WorkingTimeEntity("ccc", "ddd"));
		repository.flush();

	}
*/
/*	@PostConstruct
	public void initialData() {
		repository.save(new WorkingTimeEntity("test1","test2"));
		repository.flush();
	}
*/
}
