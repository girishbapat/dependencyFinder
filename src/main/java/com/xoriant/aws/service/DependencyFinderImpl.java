package com.xoriant.aws.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.xoriant.aws.entity.Dependency;
import com.xoriant.aws.repository.DependencyRepository;

@Service
public class DependencyFinderImpl implements DependencyFinder {

	@Autowired
	DependencyRepository dependencyRepository;
	@Autowired
	Validator validator;

	private void findDependentTables(final String nameOfTable, final Queue<String> queue,
			final List<Dependency> allDependencies) {
		// final Dependency dependenciesOfCurrentTable =
		// this.dependencyRepository.findByTableName(nameOfTable);
		final Dependency dependenciesOfCurrentTable = allDependencies.stream()
				.filter(currentDependency -> nameOfTable.equals(currentDependency.getTableName())).findFirst()
				.orElse(null);
		final List<String> commaSeperatedDependencies = dependenciesOfCurrentTable.getDependencyList();
		if (!CollectionUtils.isEmpty(commaSeperatedDependencies)) {
			for (final String currentDependentTable : commaSeperatedDependencies) {
				this.findDependentTables(currentDependentTable, queue, allDependencies);
			}
			if (!queue.contains(nameOfTable)) {
				queue.add(nameOfTable);
			}
		} else {
			if (!queue.contains(nameOfTable)) {
				queue.add(nameOfTable);
			}
			return;
		}

	}

	@Override
	public Queue<String> findDependencies(final String nameOfTable) throws RuntimeException {
		final Queue<String> queue = new LinkedList<>();
		final List<String> validationErrors = this.validate(nameOfTable);
		if (!CollectionUtils.isEmpty(validationErrors)) {
			final String strValidationErrors = StringUtils.join(validationErrors, "|");
			throw new RuntimeException(strValidationErrors);
		}
		final List<Dependency> allDependencies = this.dependencyRepository.findAll();
		this.findDependentTables(nameOfTable, queue, allDependencies);
		return queue;
	}

	@Override
	public List<String> validate(final String nameOfTable) {
		final List<Dependency> allDependencies = this.dependencyRepository.findAll();
		final List<String> validationErrors = this.validator.validate(nameOfTable, allDependencies);
		return validationErrors;
	}

	@Override
	public Queue<String> findDependencies(final String nameOfTable, final boolean needPriorValidations)
			throws RuntimeException {
		Queue<String> queue = new LinkedList<>();
		final List<Dependency> allDependencies = this.dependencyRepository.findAll();
		if (needPriorValidations) {
			queue = this.findDependencies(nameOfTable);
		} else {
			this.findDependentTables(nameOfTable, queue, allDependencies);
		}
		return queue;
	}

}
