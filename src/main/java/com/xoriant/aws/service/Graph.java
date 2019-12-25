package com.xoriant.aws.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Graph {

	private final List<Vertex> vertices;

	public Graph() {
		this.vertices = new ArrayList<>();
	}

	public Graph(final List<Vertex> vertices) {
		this.vertices = vertices;
	}

	public void addVertex(final Vertex vertex) {
		this.vertices.add(vertex);
	}

	public void addEdge(final Vertex from, final Vertex to) {
		from.addNeighbour(to);
	}

	public boolean hasCycle() {
		for (final Vertex vertex : this.vertices) {
			if (!vertex.isVisited() && this.hasCycle(vertex)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasCycle(final Vertex sourceVertex) {
		sourceVertex.setBeingVisited(true);

		for (final Vertex neighbour : sourceVertex.getAdjacencyList()) {
			if (neighbour.isBeingVisited()) {
				// backward edge exists
				return true;
			} else if (!neighbour.isVisited() && this.hasCycle(neighbour)) {
				return true;
			}
		}

		sourceVertex.setBeingVisited(false);
		sourceVertex.setVisited(true);
		return false;
	}
}