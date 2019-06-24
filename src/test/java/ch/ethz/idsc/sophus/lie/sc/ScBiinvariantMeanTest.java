// code by jph
package ch.ethz.idsc.sophus.lie.sc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ScBiinvariantMeanTest extends TestCase {
  public void testSimple() {
    Scalar scalar = ScBiinvariantMean.INSTANCE.mean(Tensors.vector(1, 2, 3), Tensors.fromString("{1/3, 1/3, 1/3}"));
    Chop._10.requireClose(scalar, RealScalar.of(1.8171205928321397));
  }
}
