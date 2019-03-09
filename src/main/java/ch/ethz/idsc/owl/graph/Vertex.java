// code by astoll
package ch.ethz.idsc.owl.graph;

import java.util.Objects;

public class Vertex<T> {
  String label;

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
