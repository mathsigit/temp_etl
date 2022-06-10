package com.island.tcbetl;

import com.island.tcbetl.entity.EntryInput;
import com.island.tcbetl.service.EntryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@SpringBootApplication
public class TcbetlApplication implements ApplicationRunner {


	@Value("${input.folder.path:#{null}}")
	private String inputFolderPath;

	@Value("${output.folder.path:#{null}}")
	private String outputFolderPath;

	@Value("${input.file.name:#{null}}")
	private String fileName;

	@Value("${input.date.file.path:#{null}}")
	private String dateFilePath;

	/**
	 * Application execute mode.
	 * file: Saving convert result as a file without compare.
	 * compare: Comparing with the file after converting result without saving as a csv file.
	 */
	@Value("${mode:#{null}}")
	private String apMode = "";

	/**
	 * (Optional) Answer file path. Required when mode=compare
	 */
	@Value("${answer.file.path:#{null}}")
	private String answerFilePath;

	@Autowired
	EntryService entryService;

	public static void main(String[] args) {
		SpringApplication.run(TcbetlApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {

		if (inputFolderPath == null)
			throw new AopConfigException("The property -Dinput.folder.path must be set!");
		if (fileName == null)
			throw new AopConfigException("The property -Dinput.file.name must be set!");
		if (dateFilePath == null)
			throw new AopConfigException("The property -Dinput.date.file.path must be set!");
		if (apMode == null) {
			if (outputFolderPath == null)
				throw new AopConfigException("The property -Doutput.folder.path must be set!");
		}else if (apMode.equals(EntryInput.modeCompare) && answerFilePath == null) {
			throw new AopConfigException("The property -Danswer.file.path must be set when -Dmode equals 'compare' !");
		}
		log.info("Application started to separate columns for file(s): {} in path: {} with date file: {}, and produce to path: {} " +
						"when application successful.",
				fileName, inputFolderPath, dateFilePath, outputFolderPath);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void execute() throws Exception {
		entryService.executed(
				EntryInput.builder()
						.inputFolderPath(this.inputFolderPath)
						.outputFolderPath(this.outputFolderPath)
						.fileName(this.fileName)
						.dateFilePath(this.dateFilePath)
						.answerFilePath(this.answerFilePath)
						.apMode(apMode)
						.build()
		);
	}

}
