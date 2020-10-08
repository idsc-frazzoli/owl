// code by jph
package ch.ethz.idsc.owl.data;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ext.Timing;

/** TimeKeeper is also used in owly3d */
public class TimeKeeper {
  private final Timing timing = Timing.started();

  public Scalar now() {
    return RealScalar.of(timing.seconds());
  }
}
