/**
 *
 */
package com.xoriant.aws.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
			validationErrors.add("FATAL ERROR. Invalid input. Name of table cannot be null or empty.");
			isFatalError = true;
		}
		return isFatalError;
	}

	private boolean validateIfTableNameExists(final String nameOfTable, final List<Dependency> allDependencies,
			final List<String> validationErrors) {
		boolean isFatalError = false;
		if (!allDependencies.stream()
				.anyMatch(currentDependency -> StringUtils.equals(currentDependency.getTableName(), nameOfTable))) {
			final String invalidInput = String.format(
					"FATAL ERROR. Invalid input. Table with name: %s does not exist in current schema.", nameOfTable);
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
				final String noIncrementaldatAvailable = String.format("No incremental data available for table: %s.",
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
			this.createVertexInstanceWithReflection(allDependencies, vertexMap, graph);
		} catch (final Exception e) {
			validationErrors.add(
					"FATAL ERROR. Problem validating cyclic dependencies. Check for exception in subsequent message.");
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
			validationErrors.add("FATAL ERROR. Cyclic dependency found.");
		}
		return isFatalError;

	}

	/**
	 * Create instance of Vertex class using reflection
	 *
	 * @param allDependencies
	 * @param vertexMap
	 * @param graph
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private void createVertexInstanceWithReflection(final List<Dependency> allDependencies,
			final Map<String, Vertex> vertexMap, final Graph graph) throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (final Dependency currentDependency : allDependencies) {
			final Constructor<Vertex> constructorStr = Vertex.class.getConstructor(String.class);
			final Vertex vertex = constructorStr.newInstance(currentDependency.getTableName());
			graph.addVertex(vertex);
			vertexMap.put(currentDependency.getTableName(), vertex);
		}
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
