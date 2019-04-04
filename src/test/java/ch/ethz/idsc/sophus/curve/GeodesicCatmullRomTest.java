// code by ob
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2ParametricDistance;
import ch.ethz.idsc.sophus.math.CentripedalKnotSpacing;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GeodesicCatmullRomTest extends TestCase {
  public void testUniformInterpolatory() {
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Tensor control = Tensors.empty();
    for (int index = 0; index < 4; index++)
      control.append(Tensors.vector(Math.random(), Math.random(), Math.random()));
    Scalar alpha = RealScalar.ZERO;
    CentripedalKnotSpacing centripedalKnotSpacing = new CentripedalKnotSpacing(alpha, Se2ParametricDistance::of);
    GeodesicCatmullRom geodesicCatmullRom = new GeodesicCatmullRom(geodesicInterface, centripedalKnotSpacing.apply(control), control);
    // FIXME OB
    // Tensor cp1 = geodesicCatmullRom.apply(RealScalar.ONE);
    // Chop._10.requireClose(cp1, control.get(1));
    // Chop._10.requireClose(geodesicCatmullRom.apply(RealScalar.of(2)), control.get(2));
  }

  public void testCentripetalInterpolatory() {
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Tensor control = Tensors.empty();
    for (int index = 0; index < 4; index++)
      control.append(Tensors.vector(Math.random(), Math.random(), Math.random()));
    Scalar alpha = RealScalar.of(Math.random());
    CentripedalKnotSpacing centripedalKnotSpacing = new CentripedalKnotSpacing(alpha, Se2ParametricDistance::of);
    GeodesicCatmullRom geodesicCatmullRom = new GeodesicCatmullRom(geodesicInterface, centripedalKnotSpacing.apply(control), control);
    // FIXME OB
    // Chop._10.requireClose(geodesicCatmullRom.apply(geodesicCatmullRom.knots().Get(1)), control.get(1));
    // Chop._10.requireClose(geodesicCatmullRom.apply(geodesicCatmullRom.knots().Get(2)), control.get(2));
  }
}
