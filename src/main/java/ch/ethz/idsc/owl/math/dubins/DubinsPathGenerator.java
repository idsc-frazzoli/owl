// code by jph
package ch.ethz.idsc.owl.math.dubins;

import java.util.stream.Stream;

public interface DubinsPathGenerator {
  Stream<DubinsPath> allValid();
}
