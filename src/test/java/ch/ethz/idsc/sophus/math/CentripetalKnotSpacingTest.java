// code by jph
package ch.ethz.idsc.sophus.math;

import java.io.IOException;

import ch.ethz.idsc.sophus.planar.Se2ParametricDistance;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class CentripetalKnotSpacingTest extends TestCase {
  public void testSimple() {
    CentripetalKnotSpacing centripetalKnotSpacing = //
        new CentripetalKnotSpacing(RealScalar.of(0.5), Se2ParametricDistance.INSTANCE);
    Tensor knots = centripetalKnotSpacing.apply(Tensors.fromString("{{1,2,3},{4,5,6},{8,9,11}}"));
    Chop._12.requireClose(knots, Tensors.vector(0, 2.525854879647931, 4.988462479155103));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    CentripetalKnotSpacing centripetalKnotSpacing = //
        Serialization.copy(new CentripetalKnotSpacing(RealScalar.of(0.5), Se2ParametricDistance.INSTANCE));
    Tensor knots = centripetalKnotSpacing.apply(Tensors.fromString("{{1,2,3},{4,5,6},{8,9,11}}"));
    Chop._12.requireClose(knots, Tensors.vector(0, 2.525854879647931, 4.988462479155103));
  }

  public void testEmpty() {
    CentripetalKnotSpacing centripetalKnotSpacing = //
        new CentripetalKnotSpacing(RealScalar.of(0.75), Se2ParametricDistance.INSTANCE);
    try {
      centripetalKnotSpacing.apply(Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertEquals(centripetalKnotSpacing.apply(Tensors.fromString("{{2,3,4}}")), Tensors.vector(0));
  }

  public void testScalarFail() {
    CentripetalKnotSpacing centripetalKnotSpacing = //
        new CentripetalKnotSpacing(RealScalar.of(0.25), Se2ParametricDistance.INSTANCE);
    try {
      centripetalKnotSpacing.apply(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
