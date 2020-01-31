package com.xoriant.aws;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Queue;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.xoriant.aws.entity.Dependency;
import com.xoriant.aws.repository.DependencyRepository;
import com.xoriant.aws.service.DependencyFinder;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DependencyApplication.class)
class DependencyApplicationTests {

	@BeforeAll
	static void initialSetup() throws IOException {
		final int NO_OF_DEPENDENCIES = 900;
		final String dataSql = "src/test/resources/data.sql";
		final StringBuffer initialSql=new StringBuffer();

		initialSql.append("DROP TABLE IF EXISTS dependency; \n");
		initialSql.append("CREATE TABLE dependency ( id INT AUTO_INCREMENT  PRIMARY KEY,\n  table_name VARCHAR(250) NOT NULL, \n dependencies VARCHAR(200000),\n etl VARCHAR(250), \n data_available INT ); \n");
		initialSql.append("INSERT INTO dependency (table_name, dependencies, etl, data_available) VALUES ");

		//write initial sql in file
		Files.write(Paths.get(dataSql), initialSql.toString().getBytes());

		StringBuffer dependencies=new StringBuffer();
		for ( int i=1;i<NO_OF_DEPENDENCIES+1;i++) {
			dependencies=new StringBuffer();
			//add value for table name like 't1','
			dependencies.append("('t"+i+"', '");
			//add values of dependencies like t2, t3,...only for every 5th row

			if(i%50!=0) {
				for(int j=i+1;j<NO_OF_DEPENDENCIES+1;j++) {
					dependencies.append("t"+j+",");
				}
				//remove last comma
				if(i<NO_OF_DEPENDENCIES) {
					dependencies.deleteCharAt(dependencies.length()-1);
				}
			}

			//add value of sql like select * from t1
			dependencies.append("', 'select * from t"+i+"', 1),");
			//remove last comma
			if(i==NO_OF_DEPENDENCIES) {
				dependencies.deleteCharAt(dependencies.length()-1);
				dependencies.append("\n;");
			}
			dependencies.append("\n");

			System.out.println(dependencies);

			//write all the contents.
			Files.write(Paths.get(dataSql), dependencies.toString().getBytes(),StandardOpenOption.APPEND);
		}

	}


	@Test
	void contextLoads() {
	}

	@Autowired
	private DependencyFinder dependencyFinder;
	@Autowired
	private DependencyRepository dependencyRepository;

	@Test
	/**
	 * Positive test case for finding dependency for given table.
	 */

	public void positiveFindDependency() {
		final LocalDateTime startTime = LocalDateTime.now();
		System.out.println("Started executing test: positiveFindDependency:" + startTime);
		final Queue<String> resolveDependency = this.dependencyFinder.findDependencies("t1");
		System.out.println("queue listing");
		System.out.println("=====================================");
		resolveDependency.forEach(System.out::println);
		System.out.println("=====================================");
		Assert.assertNotNull(resolveDependency);
		final LocalDateTime endTime = LocalDateTime.now();
		System.out.println("Completed execution of test: positiveFindDependency:" + endTime
				+ ". time required in seconds is:" + ChronoUnit.SECONDS.between(startTime, endTime));
	}

	/**
	 * Positive test case by sending whether to validate before finding dependency
	 */

	@Test
	public void positiveFindDependencyWithWhetherToValidate() {
		final LocalDateTime startTime = LocalDateTime.now();
		System.out.println("=====================================");
		System.out.println("Started executing test: positiveFindDependencyWithWhetherToValidate:" + startTime);
		final Queue<String> resolveDependency = this.dependencyFinder.findDependencies("t1", true);
		System.out.println("queue listing");
		System.out.println("=====================================");
		resolveDependency.forEach(System.out::println);
		System.out.println("=====================================");
		Assert.assertNotNull(resolveDependency);
		final LocalDateTime endTime = LocalDateTime.now();
		System.out.println("Completed execution of test: positiveFindDependencyWithWhetherToValidate:" + endTime
				+ ". time required in seconds is:" + ChronoUnit.SECONDS.between(startTime, endTime));
	}

	/**
	 * Positive test case just check if the current table can be successfully
	 * transfered or any validation errors present.
	 */

	@Test
	public void positiveJustValidateCheckIfCurrentTableCanBeSuccessfullyProcessed() {
		final LocalDateTime startTime = LocalDateTime.now();
		System.out.println("=====================================");
		System.out.println("Started executing test: positiveJustValidateCheckIfCurrentTableCanBeSuccessfullyProcessed:"
				+ startTime);

		final List<String> validationErrors = this.dependencyFinder.validate("t1");
		Assert.assertEquals("No validation errors!", Boolean.TRUE, validationErrors.size() == 0);
		final LocalDateTime endTime = LocalDateTime.now();
		System.out.println(
				"Completed execution of test: positiveJustValidateCheckIfCurrentTableCanBeSuccessfullyProcessed:"
						+ endTime + ". time required in seconds is:" + ChronoUnit.SECONDS.between(startTime, endTime));
	}

	/**
	 * Positive test case just check if the current table can be successfully
	 * transfered or any validation errors present.
	 */

	@Test
	public void positiveValidateAndSeeError() {
		final LocalDateTime startTime = LocalDateTime.now();
		System.out.println("=====================================");
		System.out.println("Started executing test: positiveValidateAndSeeError:" + startTime);

		final Dependency t2 = this.dependencyRepository.findByTableName("t2");
		t2.setDataAvailable(0);
		this.dependencyRepository.save(t2);
		final List<String> validationErrors = this.dependencyFinder.validate("t2");
		System.out.println("=====================================");
		validationErrors.forEach(System.out::println);
		System.out.println("=====================================");
		Assert.assertEquals("Got validation errors!", Boolean.TRUE, validationErrors.size() > 0);
		t2.setDataAvailable(1);
		this.dependencyRepository.save(t2);
		final LocalDateTime endTime = LocalDateTime.now();
		System.out.println("Completed execution of test: positiveValidateAndSeeError:" + endTime
				+ ". time required in seconds is:" + ChronoUnit.SECONDS.between(startTime, endTime));
	}

	@Test
	public void negativeValidateTableNameCannotBeNullOrEmptyAndThrowException() {
		final LocalDateTime startTime = LocalDateTime.now();
		System.out.println("=====================================");
		System.out.println(
				"Started executing test: negativeValidateTableNameCannotBeNullOrEmptyAndThrowException:" + startTime);
		final Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
			this.dependencyFinder.findDependencies("  ");
		});

		final String expectedMessage = "FATAL ERROR. Invalid input. Name of table cannot be null or empty.";
		final String actualMessage = exception.getMessage();
		System.out.println("=====================================");
		System.out.println(actualMessage);
		System.out.println("=====================================");
		Assert.assertTrue(actualMessage.contains(expectedMessage));

		final LocalDateTime endTime = LocalDateTime.now();
		System.out.println("Completed execution of test: negativeValidateTableNameCannotBeNullOrEmptyAndThrowException:"
				+ endTime + ". time required in seconds is:" + ChronoUnit.SECONDS.between(startTime, endTime));

	}

	@Test
	public void negativeValidateIfTableNameExistsInCurrentSchemaAndThrowException() {
		final LocalDateTime startTime = LocalDateTime.now();
		System.out.println("=====================================");
		System.out.println("Started executing test: negativeValidateIfTableNameExistsInCurrentSchemaAndThrowException:"
				+ startTime);

		final String nonExitantTableName = "xyz";

		final Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
			this.dependencyFinder.findDependencies(nonExitantTableName);
		});

		final String expectedMessage = String.format(
				"FATAL ERROR. Invalid input. Table with name: %s does not exist in current schema.",
				nonExitantTableName);
		final String actualMessage = exception.getMessage();
		System.out.println("=====================================");
		System.out.println(actualMessage);
		System.out.println("=====================================");
		Assert.assertTrue(actualMessage.contains(expectedMessage));
		final LocalDateTime endTime = LocalDateTime.now();
		System.out.println(
				"Completed execution of test: negativeValidateIfTableNameExistsInCurrentSchemaAndThrowException:"
						+ endTime + ". time required in seconds is:" + ChronoUnit.SECONDS.between(startTime, endTime));

	}

	@Ignore

	@Test
	public void negativeCheckIfNoDataAvaialbleAndThrowException() {
		final LocalDateTime startTime = LocalDateTime.now();
		System.out.println("=====================================");
		System.out.println("Started executing test: negativeCheckIfNoDataAvaialbleAndThrowException:" + startTime);

		final Dependency t2 = this.dependencyRepository.findByTableName("t2");
		t2.setDataAvailable(0);
		this.dependencyRepository.save(t2);
		final Dependency t4 = this.dependencyRepository.findByTableName("t4");
		t4.setDataAvailable(0);
		this.dependencyRepository.save(t4);
		final Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
			this.dependencyFinder.findDependencies("t1");
		});

		final String expectedMessage = "No incremental data available for table";
		final String actualMessage = exception.getMessage();
		System.out.println("=====================================");
		System.out.println(actualMessage);
		System.out.println("=====================================");
		Assert.assertTrue(actualMessage.contains(expectedMessage));

		t2.setDataAvailable(1);
		this.dependencyRepository.save(t2);
		t4.setDataAvailable(1);
		this.dependencyRepository.save(t4);
		final LocalDateTime endTime = LocalDateTime.now();
		System.out.println("Completed execution of test: negativeCheckIfNoDataAvaialbleAndThrowException:" + endTime
				+ ". time required in seconds is:" + ChronoUnit.SECONDS.between(startTime, endTime));

	}

	@Test
	public void negativeFindCyclicDependencyAndThrowException() {
		final LocalDateTime startTime = LocalDateTime.now();
		System.out.println("=====================================");
		System.out.println("Started executing test: negativeFindCyclicDependencyAndThrowException:" + startTime);

		final Dependency t899 = this.dependencyRepository.findByTableName("t899");
		t899.setDependencies("t84");
		this.dependencyRepository.save(t899);

		/*
		 * final Dependency t6 = this.dependencyRepository.findByTableName("t4");
		 * t6.setDependencies("t2,t4"); this.dependencyRepository.save(t6);
		 */

		final Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
			this.dependencyFinder.findDependencies("t1");
		});

		final String expectedMessage = "Cyclic dependency found";
		final String actualMessage = exception.getMessage();
		System.out.println("=====================================");
		System.out.println(actualMessage);
		System.out.println("=====================================");
		Assert.assertTrue(actualMessage.contains(expectedMessage));

		t899.setDependencies("");
		this.dependencyRepository.save(t899);

		/*
		 * t6.setDependencies(""); this.dependencyRepository.save(t6);
		 */

		final LocalDateTime endTime = LocalDateTime.now();
		System.out.println("Completed execution of test: negativeFindCyclicDependencyAndThrowException:" + endTime
				+ ". time required in seconds is:" + ChronoUnit.SECONDS.between(startTime, endTime));

	}

}
