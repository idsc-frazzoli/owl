// code by jph
package ch.ethz.idsc.owl.data;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** TimeKeeper is also used in owly3d */
public class TimeKeeper {
  private final Stopwatch stopwatch = Stopwatch.started();

  public Scalar now() {
    return RealScalar.of(stopwatch.display_seconds());
  }
}
