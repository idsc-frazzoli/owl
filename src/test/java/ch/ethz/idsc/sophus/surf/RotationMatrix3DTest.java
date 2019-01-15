// code by jph
package ch.ethz.idsc.sophus.surf;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RotationMatrix3DTest extends TestCase {
  public void testSimple() {
    Scalar scalar = RealScalar.of(.5);
    Tensor tensor = R3S2Geodesic.INSTANCE.split( //
        Tensors.fromString("{{0,0,0},{1,0,0}}"), //
        Tensors.fromString("{{1,0,0},{0,1,0}}"), scalar);
    Chop._11.requireClose(tensor, //
        Tensors.fromString("{{0.5, -0.20710678118654752, 0.0}, {0.7071067811865476, 0.7071067811865475, 0.0}}"));
  }
}
