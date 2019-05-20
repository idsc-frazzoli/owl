// code by astoll
package ch.ethz.idsc.owl.graph;

import java.util.Objects;

/* package */ class Vertex<T> {
  private final String label;
  public T value;

  public Vertex(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Vertex<?> //
        && this.label.equals(((Vertex<?>) object).label);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label);
  }
}
