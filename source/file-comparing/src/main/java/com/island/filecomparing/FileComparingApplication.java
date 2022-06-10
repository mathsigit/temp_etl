package com.island.filecomparing;

import com.island.filecomparing.service.EntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.File;

@SpringBootApplication
public class FileComparingApplication implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(FileComparingApplication.class);

	@Value("${compare.file.path:#{null}}")
	private String compareFilePath;

	@Value("${answer.file.path:#{null}}")
	private String answerFilePath;

	@Autowired
	EntryService entryService;

	public static void main(String[] args) {
		SpringApplication.run(FileComparingApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (compareFilePath == null)
			throw new AopConfigException("The property -Dcompare.file.path must be set!");
		if (answerFilePath == null)
			throw new AopConfigException("The property -Danswer.file.path must be set!");

		logger.info("Application started to compare values of {} and {} ", compareFilePath, answerFilePath);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void execute() throws Exception {
		entryService.executed(compareFilePath, answerFilePath);
	}
}
