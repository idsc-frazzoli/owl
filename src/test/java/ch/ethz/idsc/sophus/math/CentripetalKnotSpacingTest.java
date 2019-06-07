// code by jph
package ch.ethz.idsc.sophus.math;

import java.io.IOException;

import ch.ethz.idsc.sophus.planar.Se2ParametricDistance;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class CentripetalKnotSpacingTest extends TestCase {
  public void testSimple() {
    TensorUnaryOperator centripetalKnotSpacing = //
        CentripetalKnotSpacing.of(Se2ParametricDistance.INSTANCE, 0.5);
    Tensor knots = centripetalKnotSpacing.apply(Tensors.fromString("{{1,2,3},{4,5,6},{8,9,11}}"));
    Chop._12.requireClose(knots, Tensors.vector(0, 2.525854879647931, 4.988462479155103));
  }

  public void testUniform() {
    TensorUnaryOperator centripetalKnotSpacing = //
        CentripetalKnotSpacing.uniform(Se2ParametricDistance.INSTANCE);
    Tensor knots = centripetalKnotSpacing.apply(Tensors.fromString("{{1,2,3},{4,5,6},{8,9,11}}"));
    assertEquals(knots, Range.of(0, 3));
  }

  public void testChordal() {
    TensorUnaryOperator chordal = //
        CentripetalKnotSpacing.chordal(Se2ParametricDistance.INSTANCE);
    TensorUnaryOperator power_1 = //
        CentripetalKnotSpacing.of(Se2ParametricDistance.INSTANCE, 1);
    Tensor control = Tensors.fromString("{{1,2,3},{4,5,6},{8,9,11}}");
    assertEquals(chordal.apply(control), power_1.apply(control));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator centripetalKnotSpacing = //
        Serialization.copy(CentripetalKnotSpacing.of(Se2ParametricDistance.INSTANCE, 0.5));
    Tensor knots = centripetalKnotSpacing.apply(Tensors.fromString("{{1,2,3},{4,5,6},{8,9,11}}"));
    Chop._12.requireClose(knots, Tensors.vector(0, 2.525854879647931, 4.988462479155103));
  }

  public void testEmpty() {
    TensorUnaryOperator centripetalKnotSpacing = CentripetalKnotSpacing.of(Se2ParametricDistance.INSTANCE, 0.75);
    try {
      centripetalKnotSpacing.apply(Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertEquals(centripetalKnotSpacing.apply(Tensors.fromString("{{2,3,4}}")), Tensors.vector(0));
  }

  public void testScalarFail() {
    TensorUnaryOperator centripetalKnotSpacing = CentripetalKnotSpacing.of(Se2ParametricDistance.INSTANCE, 0.25);
    try {
      centripetalKnotSpacing.apply(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
