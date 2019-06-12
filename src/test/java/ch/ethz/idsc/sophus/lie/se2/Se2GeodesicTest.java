// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2GeodesicTest extends TestCase {
  public void testSimple() {
    Tensor split = Se2Geodesic.INSTANCE.split(Tensors.vector(0, 0, 0), Tensors.vector(10, 0, 1), RealScalar.of(.7));
    Chop._13.requireClose(split, Tensors.fromString("{7.071951896570488, -1.0688209919859546, 0.7}"));
  }
}
