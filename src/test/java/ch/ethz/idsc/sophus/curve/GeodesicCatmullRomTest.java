package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicCatmullRomTest extends TestCase {
  public void testUniformInterpolatory() {
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Tensor control = Tensors.empty();
    for (int index = 0; index < 4; index++)
      control.append(Tensors.vector(Math.random(), Math.random(), Math.random()));
    Scalar alpha = RealScalar.ZERO;
    GeodesicCatmullRom geodesicCatmullRom = new GeodesicCatmullRom(geodesicInterface, control, alpha);
    Chop._10.requireClose(geodesicCatmullRom.apply(RealScalar.ONE), control.get(1));
    Chop._10.requireClose(geodesicCatmullRom.apply(RealScalar.of(2)), control.get(2));
  }

  public void testCentripetalInterpolatory() {
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Tensor control = Tensors.empty();
    for (int index = 0; index < 4; index++)
      control.append(Tensors.vector(Math.random(), Math.random(), Math.random()));
    Scalar alpha = RealScalar.of(Math.random());
    GeodesicCatmullRom geodesicCatmullRom = new GeodesicCatmullRom(geodesicInterface, control, alpha);
    Chop._10.requireClose(geodesicCatmullRom.apply(geodesicCatmullRom.knots().Get(1)), control.get(1));
    Chop._10.requireClose(geodesicCatmullRom.apply(geodesicCatmullRom.knots().Get(2)), control.get(2));
  }
}
