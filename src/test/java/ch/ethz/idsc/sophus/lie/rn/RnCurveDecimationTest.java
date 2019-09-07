// code by jph
package ch.ethz.idsc.sophus.lie.rn;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class RnCurveDecimationTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TensorUnaryOperator rnCurveDecimation = Serialization.copy(RnCurveDecimation.of(RealScalar.ZERO));
    Tensor tensor = TensorProduct.of(Range.of(0, 3), UnitVector.of(2, 0));
    assertEquals(tensor, Tensors.fromString("{{0, 0}, {1, 0}, {2, 0}}"));
    Tensor apply = rnCurveDecimation.apply(tensor);
    assertEquals(apply, Tensors.fromString("{{0, 0}, {2, 0}}"));
  }

  public void testEmpty() {
    TensorUnaryOperator rnCurveDecimation = RnCurveDecimation.of(RealScalar.ONE);
    Tensor tensor = rnCurveDecimation.apply(Tensors.empty());
    assertEquals(tensor, Tensors.empty());
  }

  public void testSingle() {
    TensorUnaryOperator rnCurveDecimation = RnCurveDecimation.of(RealScalar.ONE);
    Tensor input = Tensors.of(Tensors.vector(1, 2, 3)).unmodifiable();
    Tensor tensor = rnCurveDecimation.apply(input);
    assertEquals(tensor, input);
  }
}
