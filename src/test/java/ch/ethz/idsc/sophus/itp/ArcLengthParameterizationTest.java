// code by jph
package ch.ethz.idsc.sophus.itp;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import junit.framework.TestCase;

public class ArcLengthParameterizationTest extends TestCase {
  public void testSimpleR2String() {
    ScalarTensorFunction scalarTensorFunction = ArcLengthParameterization.of(Tensors.vector(1, 1, 1, 1), RnGeodesic.INSTANCE, Range.of(0, 5));
    assertEquals(ExactTensorQ.require(scalarTensorFunction.apply(RealScalar.ZERO)), RealScalar.of(0));
    assertEquals(ExactTensorQ.require(scalarTensorFunction.apply(RationalScalar.HALF)), RealScalar.of(2));
    assertEquals(ExactTensorQ.require(scalarTensorFunction.apply(RealScalar.ONE)), RealScalar.of(4));
    try {
      scalarTensorFunction.apply(RealScalar.of(-0.1));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      scalarTensorFunction.apply(RealScalar.of(1.1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailLength() {
    try {
      ArcLengthParameterization.of(Tensors.vector(1, 2, 3), RnGeodesic.INSTANCE, Tensors.vector(1, 2, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailNonVector() {
    try {
      ArcLengthParameterization.of(HilbertMatrix.of(3), RnGeodesic.INSTANCE, Tensors.vector(1, 2, 3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailNull() {
    try {
      ArcLengthParameterization.of(Tensors.vector(1, 2, 3), null, Tensors.vector(1, 2, 3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
