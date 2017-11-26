// code by jph
package ch.ethz.idsc.owl.data;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** TimeKeeper is also used in owly3d */
public class TimeKeeper {
  private final long tic = System.nanoTime();

  public Scalar now() {
    long toc = System.nanoTime();
    double delta = (toc - tic) * 1e-9;
    return RealScalar.of(delta);
  }
}
