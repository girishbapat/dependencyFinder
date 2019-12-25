package com.xoriant.aws.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Vertex {

	private String label;

	private boolean visited;

	private boolean beingVisited;

	private List<Vertex> adjacencyList;

	public Vertex() {
		super();
	}

	public Vertex(final String label) {
		super();
		this.label = label;
		this.adjacencyList = new ArrayList<>();
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public boolean isVisited() {
		return this.visited;
	}

	public void setVisited(final boolean visited) {
		this.visited = visited;
	}

	public boolean isBeingVisited() {
		return this.beingVisited;
	}

	public void setBeingVisited(final boolean beingVisited) {
		this.beingVisited = beingVisited;
	}

	public List<Vertex> getAdjacencyList() {
		return this.adjacencyList;
	}

	public void setAdjacencyList(final List<Vertex> adjacencyList) {
		this.adjacencyList = adjacencyList;
	}

	public void addNeighbour(final Vertex adjacent) {
		this.adjacencyList.add(adjacent);
	}
}