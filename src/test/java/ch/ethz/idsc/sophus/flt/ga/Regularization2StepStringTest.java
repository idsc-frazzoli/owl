// code by jph
package ch.ethz.idsc.sophus.flt.ga;

import java.io.IOException;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class Regularization2StepStringTest extends TestCase {
  private static final TensorUnaryOperator STRING = //
      Regularization2Step.string(RnGeodesic.INSTANCE, RationalScalar.of(1, 4));

  public void testLo() throws ClassNotFoundException, IOException {
    Tensor signal = Tensors.vector(1, 0, 0, 0, 0);
    Tensor tensor = Serialization.copy(STRING).apply(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.vector(1, 0.125, 0, 0, 0));
    TensorUnaryOperator tensorUnaryOperator = Regularization2Step.string(RnGeodesic.INSTANCE, RealScalar.of(0.25));
    assertEquals(tensor, tensorUnaryOperator.apply(signal));
  }

  public void testHi() {
    Tensor signal = Tensors.vector(0, 0, 0, 0, 1);
    Tensor tensor = STRING.apply(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.vector(0, 0, 0, 0.125, 1));
    TensorUnaryOperator tensorUnaryOperator = Regularization2Step.string(RnGeodesic.INSTANCE, RealScalar.of(0.25));
    assertEquals(tensor, tensorUnaryOperator.apply(signal));
  }

  public void testEmpty() {
    assertEquals(STRING.apply(Tensors.empty()), Tensors.empty());
  }

  public void testSingle() {
    assertEquals(STRING.apply(Tensors.vector(2)), Tensors.vector(2));
  }

  public void testSimple() {
    TensorUnaryOperator STRING = //
        Regularization2Step.string(RnGeodesic.INSTANCE, RationalScalar.of(1, 2));
    Tensor signal = Tensors.vector(1, 1, 1, 2, 1, 1, 1, 1, 1, 1);
    Tensor tensor = STRING.apply(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.fromString("{1, 1, 5/4, 3/2, 5/4, 1, 1, 1, 1, 1}"));
  }

  public void testMatrix() {
    TensorUnaryOperator STRING = //
        Regularization2Step.string(RnGeodesic.INSTANCE, RationalScalar.of(1, 2));
    Tensor signal = Tensors.fromString("{{1,2},{2,2},{3,2},{4,2},{3,3}}");
    Tensor tensor = STRING.apply(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.fromString("{{1, 2}, {2, 2}, {3, 2}, {7/2, 9/4}, {3, 3}}"));
  }

  public void testZero() {
    Tensor signal = Tensors.vector(1, 1, 1, 2, 1, 1, 3, 1, 1, 1);
    Tensor tensor = Regularization2Step.string(RnGeodesic.INSTANCE, RealScalar.ZERO).apply(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, signal);
  }
}
