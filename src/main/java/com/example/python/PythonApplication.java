package com.example.python;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class PythonApplication {


	public static void main(String[] args) {
		SpringApplication.run(PythonApplication.class, args);

	}
}
