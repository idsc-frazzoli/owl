// code by astoll
package ch.ethz.idsc.owl.graph;

/* package */ class Edge<T> {
  public final Vertex<T> source;
  public final Vertex<T> target;

  public Edge(Vertex<T> source, Vertex<T> target) {
    this.source = source;
    this.target = target;
  }

  public Vertex<T> getSource() {
    return this.source;
  }

  public Vertex<T> getTarget() {
    return this.target;
  }
}
