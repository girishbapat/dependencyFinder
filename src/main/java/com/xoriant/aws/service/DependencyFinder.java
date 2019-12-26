/**
 *
 */
package com.xoriant.aws.service;

import java.util.List;
import java.util.Queue;

/**
 * @author Bapat_G
 *
 */
public interface DependencyFinder {
	/**
	 * API to find dependencies for given table. This api first performs following
	 * validations for given table name:<br>
	 * 1. Whether incremental data is available for current table and all the
	 * dependent tables or not<br>
	 * 2. Whether if there is any circular dependency is present <br>
	 * <strong>If any of the validations fails, it does not proceed to find
	 * dependencies <br>
	 * else generates dependencies in a queue data structure.<br>
	 * The caller needs to process the tables according to order</strong>
	 *
	 * @param nameOfTable -- Name of table for which validations to be performed.
	 * @return-- Queue of names of tables of resolved dependencies.
	 * @throws RuntimeException--exceptions in case of any validation fails
	 *
	 */
	Queue<String> findDependencies(String nameOfTable) throws RuntimeException;

	/**
	 * This api performs following validations for given table name:<br>
	 * 1. Whether given table name empty or null.- <b>Fatal Error</b><br>
	 * 2. Whether given table name exists in schema or not.- <b>Fatal Error</b><br>
	 * 3. Whether incremental data is available for current table and all the
	 * dependent tables or not<br>
	 * 4. Whether if there is any circular dependency is present.- <b>Fatal
	 * Error</b><br>
	 * It returns consolidated validation issues in List. <br>
	 * <strong>Note: if the issue is fatal then it immediately returns to caller,
	 * else continues with validations</strong>
	 *
	 * @param nameOfTable
	 * @return list of validation issues
	 */
	List<String> validate(String nameOfTable);

	/**
	 * This API is same as basic <strong> Queue<String> findDependencies(String
	 * nameOfTable )throws RuntimeException;</strong> except<br>
	 * this API accepts boolean value, depending upon same end user can decide to
	 * perform validations or not default is true
	 *
	 * @param nameOfTable
	 * @param needPriorValidations- default true
	 * @return
	 * @throws RuntimeException
	 */
	Queue<String> findDependencies(String nameOfTable, boolean needPriorValidations) throws RuntimeException;

}
