package com.xoriant.aws.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.junit.platform.commons.util.StringUtils;

import lombok.Data;


@Data
@Entity
@Table(name = "dependency")
public class Dependency {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name ="table_name")
	private String tableName;
	private String dependencies;
	private String etl;
	@Column(name ="data_available")
	private int dataAvailable;


	public Dependency() {
		// TODO Auto-generated constructor stub
	}
	public List<String> getDependencyList(){
		List<String> dependencyList=new ArrayList<>();
		if(StringUtils.isNotBlank(this.getDependencies())) {
			dependencyList= Arrays.asList(this.getDependencies().split("\\s*,\\s*"));
		}
		return dependencyList;
	}

}
