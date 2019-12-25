package com.xoriant.aws;

import java.util.Queue;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
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

	@Test
	void contextLoads() {
	}

	@Autowired
	private DependencyFinder dependencyFinder;
	@Autowired
	private DependencyRepository dependencyRepository;

	@Test
	public void positiveFindDependencyAndPrintOutput() {
		final Queue<String> resolveDependency = this.dependencyFinder.findDependencies("t1");
		System.out.println("queue listing");
		System.out.println("=====================================");
		resolveDependency.forEach(System.out::println);
		System.out.println("=====================================");
		Assert.assertNotNull(resolveDependency);
	}

	@Test
	public void negativeValidateTableNameCannotBeNullOrEmptyAndThrowException() {
		final Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
			this.dependencyFinder.findDependencies("  ");
		});

		final String expectedMessage = "Invalid input. Name of table cannot be null or empty.";
		final String actualMessage = exception.getMessage();
		System.out.println("=====================================");
		System.out.println(actualMessage);
		System.out.println("=====================================");
		Assert.assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	public void negativeValidateIfTableNameExistsInCurrentSchemaAndThrowException() {
		final String nonExitantTableName = "xyz";

		final Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
			this.dependencyFinder.findDependencies(nonExitantTableName);
		});

		final String expectedMessage = String
				.format("Invalid input. Table with name: %s does not exists in current schema.", nonExitantTableName);
		final String actualMessage = exception.getMessage();
		System.out.println("=====================================");
		System.out.println(actualMessage);
		System.out.println("=====================================");
		Assert.assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	public void negativeCheckIfNoDataAvaialbleAndThrowException() {
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
	}

	@Test
	public void negativeFindCyclicDependencyAndThrowException() {
		final Dependency t3 = this.dependencyRepository.findByTableName("t3");
		t3.setDependencies("t1");
		this.dependencyRepository.save(t3);
		final Dependency t5 = this.dependencyRepository.findByTableName("t5");
		t5.setDependencies("t2,t4");
		this.dependencyRepository.save(t5);
		final Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
			this.dependencyFinder.findDependencies("t1");
		});

		final String expectedMessage = "Cyclic dependency found";
		final String actualMessage = exception.getMessage();
		System.out.println("=====================================");
		System.out.println(actualMessage);
		System.out.println("=====================================");
		Assert.assertTrue(actualMessage.contains(expectedMessage));

		t3.setDependencies("");
		this.dependencyRepository.save(t3);
		t5.setDependencies("");
		this.dependencyRepository.save(t5);
	}

}
