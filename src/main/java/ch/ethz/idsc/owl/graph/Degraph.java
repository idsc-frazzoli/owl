// code by jph
package ch.ethz.idsc.owl.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/** T is the type of vertex label */
// TODO JPH/ASTOLL class name is not final
public class Degraph<T> {
  private final Collection<Vert<T>> set = new HashSet<>();

  public Vert<T> createSingletonVert(T type) {
    Vert<T> vert = new Vert<>(type);
    set.add(vert);
    return vert;
  }

  public Collection<Vert<T>> verts() {
    return Collections.unmodifiableCollection(set);
  }
}
