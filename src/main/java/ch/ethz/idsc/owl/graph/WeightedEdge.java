// code by astoll
package ch.ethz.idsc.owl.graph;

public class WeightedEdge<T> {
  public final Vertex<T> start;
  public final Vertex<T> end;
  public final T weight;

  public WeightedEdge(Vertex<T> start, Vertex<T> end, T weight) {
    this.start = start;
    this.end = end;
    this.weight = weight;
  }

  public Vertex<T> getStart() {
    return this.start;
  }

  public Vertex<T> getEnd() {
    return this.end;
  }

  public T getWeight() {
    return this.weight;
  }
}
