/**
 *
 */
package com.xoriant.aws.service;

import java.util.List;

import com.xoriant.aws.entity.Dependency;

/**
 * @author Bapat_G
 *
 */
public interface Validator {
	List<String> validate(String nameOfTable, List<Dependency> allDependencies);
}
