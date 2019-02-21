package com.sarva.logapp;

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
public class LogAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogAppApplication.class, args);
	}

}

@RestController
class GreetingsController {
	
	@Value("${app.greetings.name:anonymous}")
	private String greetingsName;
	
	@GetMapping("/greetings")
	public String greetings() {
		return "hello " + greetingsName;
	}
}

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
class DemoController {

	List<CarInventory> carsInventory = new ArrayList<>();

	public DemoController() {
		setUp();
	}

	public void setUp() {
		carsInventory.add(new CarInventory("Toyota", "Camry", 10));
		carsInventory.add(new CarInventory("Honda", "Civic", 20));
		carsInventory.add(new CarInventory("Audi", "Q5", 3));
	}

	@PostMapping("build-cars-inventory")
	public List<CarInventory> buildCarsInventory() {
		carsInventory = new ArrayList<>();
		setUp();
		return carsInventory;
	}

	@PostMapping("/cars-inventory")
	public List<CarInventory> addCar(@RequestBody CarInventory carInventory) {
		carsInventory.add(carInventory);
		log.info("new car is added to the inventory " + carInventory);
		log.info("car inventoy is created {} ", kv("carinventory", carInventory) );
		
		Map<String, Object> logEntry = new HashMap<>();
		logEntry.put("carsMake", carInventory.getMake());
		logEntry.put("carsModel", carInventory.getModel());
		logEntry.put("carsStock", carInventory.getInStock());
		log.info("new one " + logEntry);
		log.info("car inventoy map is created {} ", kv("logEntry", logEntry) );

		return carsInventory;
	}

	@GetMapping("/cars-inventory")
	public List<CarInventory> getCars() {
		return carsInventory;
	}

	@GetMapping("/cars-inventory/exceptions")
	public void getExceptions() {
		try {
			int output = 10 / 0;
		} catch (Exception ex) {
			log.info("error is " + ex);
			log.error("problem occured {} ", kv("stack", ex));
		}
	}


	@PutMapping("/cars-inventory")
	public String clearCars() {
		carsInventory = new ArrayList<>();
		return "cleared all the cars from inventory";
	}

}

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
class CarInventory {

	String make;

	String model;

	int inStock;

}