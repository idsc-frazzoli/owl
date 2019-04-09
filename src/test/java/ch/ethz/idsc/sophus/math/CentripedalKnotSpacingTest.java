// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.sophus.group.Se2ParametricDistance;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class CentripedalKnotSpacingTest extends TestCase {
  public void testSimple() {
    CentripedalKnotSpacing centripedalKnotSpacing = //
        new CentripedalKnotSpacing(RealScalar.of(0.5), Se2ParametricDistance::of);
    Tensor knots = centripedalKnotSpacing.apply(Tensors.fromString("{{1,2,3},{4,5,6},{8,9,11}}"));
    Chop._12.requireClose(knots, Tensors.vector(0, 2.525854879647931, 4.988462479155103));
  }
}
