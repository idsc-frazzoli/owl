// code by astoll, jph
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;

public interface MinTrackerInterface<T> {
  void digest(T x);

  Collection<T> getMinElements();
}
