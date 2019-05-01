// code by jph
package ch.ethz.idsc.owl.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ch.ethz.idsc.tensor.Scalar;

/** T is the type of vertex label */
// TODO JPH/ASTOLL class name is not final
/* package */ class Vert<T> {
  private final Collection<Vert<T>> parents = new HashSet<>();
  private final Map<Vert<T>, Scalar> children = new HashMap<>();
  private final T label;

  /* package */ Vert(T label) {
    this.label = label;
  }

  public void insertLinkTo(Vert<T> vert, Scalar value) {
    if (children.containsKey(vert))
      throw new RuntimeException();
    children.put(vert, value);
    // ---
    if (vert.parents.contains(this))
      throw new RuntimeException();
    vert.parents.add(this);
  }

  public void removeLinkTo(Vert<T> vert) {
    // TODO ASTOLL
  }

  public T label() {
    return label;
  }

  public Map<Vert<T>, Scalar> children() {
    return Collections.unmodifiableMap(children);
  }

  public Collection<Vert<T>> parents() {
    return Collections.unmodifiableCollection(parents);
  }
}
