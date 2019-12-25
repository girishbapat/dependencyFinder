/**
 *
 */
package com.xoriant.aws.service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xoriant.aws.entity.Dependency;
import com.xoriant.aws.repository.DependencyRepository;

/**
 * @author Bapat_G
 *
 */
@Service
public class ValidatorImpl implements Validator {
	@Autowired
	DependencyRepository dependencyRepository;

	/**
	 *
	 */
	public ValidatorImpl() {
		// TODO Auto-generated constructor stub
	}

	private boolean validateTableName(final String nameOfTable, final List<String> validationErrors) {
		boolean isFatalError = false;
		if (StringUtils.isBlank(nameOfTable)) {
			validationErrors.add("Invalid input. Name of table cannot be null or empty.");
			isFatalError = true;
		}
		return isFatalError;
	}

	private boolean validateIfTableNameExists(final String nameOfTable, final List<Dependency> allDependencies,
			final List<String> validationErrors) {
		boolean isFatalError = false;
		if (!allDependencies.stream()
				.anyMatch(currentDependency -> StringUtils.equals(currentDependency.getTableName(), nameOfTable))) {
			final String invalidInput = String
					.format("Invalid input. Table with name: %s does not exists in current schema.", nameOfTable);
			validationErrors.add(invalidInput);
			isFatalError = true;
		}
		return isFatalError;
	}

	private boolean validateDataAvailability(final List<Dependency> allDependencies,
			final List<String> validationErrors) {
		final boolean isFatalError = false;

		for (final Dependency currentDependency : allDependencies) {
			if (currentDependency.getDataAvailable() == 0) {
				final String noIncrementaldatAvailable = String.format("No incremental data available for table: %s",
						currentDependency.getTableName());
				validationErrors.add(noIncrementaldatAvailable);
			}
		}

		return isFatalError;
	}

	private boolean validateCyclicDependency(final String nameOfTable, final List<Dependency> allDependencies,
			final List<String> validationErrors) {
		boolean isFatalError = false;
		final Map<String, Vertex> vertexMap = new HashMap<>();
		final Graph graph = new Graph();
		try {
			for (final Dependency currentDependency : allDependencies) {
				final Constructor<Vertex> constructorStr = Vertex.class.getConstructor(String.class);
				final Vertex vertex = constructorStr.newInstance(currentDependency.getTableName());
				graph.addVertex(vertex);
				vertexMap.put(currentDependency.getTableName(), vertex);
			}
		} catch (final Exception e) {
			validationErrors.add("Problem validating cyclic dependencies. Check for exception message");
			validationErrors.add(e.getMessage());
			return true;
		}
		for (final Dependency dependency : allDependencies) {
			final List<String> currentDependencyList = dependency.getDependencyList();
			for (final String currentDependency : currentDependencyList) {
				graph.addEdge(vertexMap.get(dependency.getTableName()), vertexMap.get(currentDependency));
			}
		}
		isFatalError = graph.hasCycle();
		if (isFatalError) {
			validationErrors.add("Cyclic dependency found");
		}
		return isFatalError;

	}

	@Override
	public List<String> validate(final String nameOfTable, final List<Dependency> allDependencies) {
		final List<String> validationErrors = new ArrayList<>();
		boolean isFatalError = this.validateTableName(nameOfTable, validationErrors);
		if (isFatalError) {
			return validationErrors;
		}
		isFatalError = this.validateIfTableNameExists(nameOfTable, allDependencies, validationErrors);
		if (isFatalError) {
			return validationErrors;
		}
		isFatalError = this.validateDataAvailability(allDependencies, validationErrors);
		if (isFatalError) {
			return validationErrors;
		}
		isFatalError = this.validateCyclicDependency(nameOfTable, allDependencies, validationErrors);
		if (isFatalError) {
			return validationErrors;
		}
		return validationErrors;
	}

}