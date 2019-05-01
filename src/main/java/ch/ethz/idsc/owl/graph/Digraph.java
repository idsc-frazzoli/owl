// code by astoll
package ch.ethz.idsc.owl.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* package */ class Digraph<T> {
  List<Vertex<T>> vertexSet;
  List<Edge<T>> edgeSet;

  public Digraph() {
    this.edgeSet = new LinkedList<>();
    this.vertexSet = new LinkedList<>();
  }

  public Digraph<T> graphBuilder(List<String> labels, boolean[][] adjMatrix) {
    Digraph<T> digraph = new Digraph<>();
    // TODO ASTOLL test for square matrix if (adjMatrix.)
    Iterator<String> iterator = labels.iterator();
    while (iterator.hasNext()) {
      digraph.addVertex(iterator.toString());
    }
    int n = adjMatrix.length;
    for (int row = 0; row < n; ++row) {
      for (int column = 0; column < n; ++column) {
        if (adjMatrix[row][column]) {
          digraph.addEdge(vertexSet.get(row), vertexSet.get(column));
        }
      }
    }
    return digraph;
  }
  // public static Digraph graphBuilder(csv File) {
  //
  // }

  public int size() {
    return vertexSet.size();
  }

  public void addEdge(Edge<T> edge) {
    edgeSet.add(edge);
    vertexSet.add(edge.getSource());
    vertexSet.add(edge.getTarget());
  }

  public void addEdge(Vertex<T> vertexU, Vertex<T> vertexV) {
    edgeSet.add(new Edge<>(vertexU, vertexV));
  }

  public void addVertex(Vertex<T> vertex) {
    vertexSet.add(vertex);
  }

  public void addVertex(String label) {
    Vertex<T> vertex = new Vertex<>(label);
    vertexSet.add(vertex);
  }
  // public ??? adjMatrix(){
  //
  // }
}