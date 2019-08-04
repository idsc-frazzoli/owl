// code by ob
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.hs.r2.Se2ParametricDistance;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.KnotSpacing;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicCatmullRomTest extends TestCase {
  public void testUniformInterpolatory() {
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Tensor control = RandomVariate.of(UniformDistribution.unit(), 5, 3);
    TensorUnaryOperator centripedalKnotSpacing = KnotSpacing.uniform();
    Tensor knots = centripedalKnotSpacing.apply(control);
    GeodesicCatmullRom geodesicCatmullRom = GeodesicCatmullRom.of(geodesicInterface, knots, control);
    // ---
    Tensor actual1 = geodesicCatmullRom.apply(RealScalar.of(1));
    Tensor expected1 = control.get(1);
    // ----
    Tensor actual2 = geodesicCatmullRom.apply(RealScalar.of(2));
    Tensor expected2 = control.get(2);
    // ----
    Chop._10.requireClose(actual2, expected2);
    Chop._10.requireClose(actual1, expected1);
  }

  public void testCentripetalInterpolatory() {
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Tensor control = Tensors.empty();
    for (int index = 0; index < 5; index++)
      control.append(Tensors.vector(Math.random(), Math.random(), Math.random()));
    TensorUnaryOperator centripedalKnotSpacing = //
        KnotSpacing.centripetal(Se2ParametricDistance.INSTANCE, RealScalar.of(Math.random()));
    Tensor knots = centripedalKnotSpacing.apply(control);
    GeodesicCatmullRom geodesicCatmullRom = GeodesicCatmullRom.of(geodesicInterface, knots, control);
    // ---
    Chop._10.requireClose(geodesicCatmullRom.apply(geodesicCatmullRom.knots().Get(1)), control.get(1));
    Chop._10.requireClose(geodesicCatmullRom.apply(geodesicCatmullRom.knots().Get(2)), control.get(2));
  }
}
