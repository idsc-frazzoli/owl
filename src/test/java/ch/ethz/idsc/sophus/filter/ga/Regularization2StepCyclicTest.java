// code by jph
package ch.ethz.idsc.sophus.filter.ga;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class Regularization2StepCyclicTest extends TestCase {
  private static final TensorUnaryOperator CYCLIC = //
      Regularization2Step.cyclic(RnGeodesic.INSTANCE, RationalScalar.of(1, 4));

  public void testLo() {
    Tensor signal = Tensors.vector(1, 0, 0, 0, 0);
    Tensor tensor = CYCLIC.apply(signal);
    assertEquals(tensor, Tensors.fromString("{3/4, 1/8, 0, 0, 1/8}"));
    TensorUnaryOperator regularization2StepCyclic = Regularization2Step.cyclic(RnGeodesic.INSTANCE, RealScalar.of(0.25));
    assertEquals(tensor, regularization2StepCyclic.apply(signal));
  }

  public void testHi() {
    Tensor signal = Tensors.vector(0, 0, 0, 0, 1);
    Tensor tensor = CYCLIC.apply(signal);
    assertEquals(tensor, Tensors.fromString("{1/8, 0, 0, 1/8, 3/4}"));
    TensorUnaryOperator tensorUnaryOperator = Regularization2Step.cyclic(RnGeodesic.INSTANCE, RealScalar.of(0.25));
    assertEquals(tensor, tensorUnaryOperator.apply(signal));
  }

  public void testEmpty() {
    assertEquals(CYCLIC.apply(Tensors.empty()), Tensors.empty());
  }

  public void testSingle() {
    assertEquals(CYCLIC.apply(Tensors.vector(3)), Tensors.vector(3));
  }

  public void testZero() {
    Tensor signal = Tensors.vector(1, 1, 1, 2, 1, 1, 3, 1, 1, 1);
    Tensor tensor = Regularization2Step.cyclic(RnGeodesic.INSTANCE, RealScalar.ZERO).apply(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, signal);
  }
}
