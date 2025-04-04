package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	/*
	В Spring Framework существует несколько ключевых аннотаций, которые используются для определения компонентов и их ролей в приложении.

	@Bean	Определяет бин в конфигурационном классе.
	@Component	Базовая аннотация для любого компонента.
	@Service	Специализированная аннотация для бизнес-логики.
	@Repository	Специализированная аннотация для работы с данными (DAO/репозитории).
	@Controller	Специализированная аннотация для обработки HTTP-запросов (MVC).
	@RestController	Специализированная версия @Controller для RESTful веб-сервисов.
	@Configuration	Используется для конфигурационных классов, где определяются бины.

	@Component — это базовая аннотация, от которой наследуются @Service, @Repository и @Controller.
	@Bean используется для ручного определения бинов в конфигурационных классах (@Configuration).
	@Service, @Repository и @Controller добавляют семантический смысл, делая код более читаемым и понятным.
	@RestController упрощает создание RESTful API, автоматически сериализуя данные в JSON/XML.*/
}
