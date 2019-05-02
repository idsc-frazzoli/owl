// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClothoidDistanceTest extends TestCase {
  public void testSimple() {
    Scalar scalar = ClothoidDistance.INSTANCE.norm(Tensors.fromString("{1[m],2[m],.3}"));
    Clips.interval(Quantity.of(2.4, "m"), Quantity.of(2.5, "m")).requireInside(scalar);
  }
}
