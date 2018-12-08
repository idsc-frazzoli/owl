// code by jph
package ch.ethz.idsc.owl.math.dubins;

import java.util.stream.Stream;

public interface DubinsPathGenerator {
  /** @return stream of all valid dubins paths with at least 2 and at most 6 elements */
  Stream<DubinsPath> allValid();
}
