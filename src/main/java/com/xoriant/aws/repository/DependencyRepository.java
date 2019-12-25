/**
 *
 */
package com.xoriant.aws.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xoriant.aws.entity.Dependency;

/**
 * @author Bapat_G
 *
 */
public interface DependencyRepository extends JpaRepository<Dependency, Integer> {
	Dependency findByTableName(String tableName);

}
