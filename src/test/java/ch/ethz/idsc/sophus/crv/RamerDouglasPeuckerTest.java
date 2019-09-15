// code by jph
package ch.ethz.idsc.sophus.crv;

import ch.ethz.idsc.sophus.lie.rn.RnCurveDecimation;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RamerDouglasPeuckerTest extends TestCase {
  public void testEmpty() {
    Tensor tensor = Tensors.empty();
    assertEquals(RnCurveDecimation.of(RealScalar.of(1)).apply(tensor), tensor);
  }

  public void testPoints1() {
    Tensor tensor = Tensors.matrix(new Number[][] { { 1, 1 } });
    assertEquals(RnCurveDecimation.of(RealScalar.of(1)).apply(tensor), tensor);
  }

  public void testPoints1copy() {
    Tensor origin = Tensors.matrix(new Number[][] { { 1, 1 } });
    Tensor tensor = Tensors.matrix(new Number[][] { { 1, 1 } });
    Tensor result = RnCurveDecimation.of(RealScalar.of(1)).apply(tensor);
    result.set(Scalar::zero, Tensor.ALL, Tensor.ALL);
    assertEquals(origin, tensor);
  }

  public void testPoints2() {
    Tensor tensor = Tensors.matrix(new Number[][] { { 1, 1 }, { 5, 2 } });
    assertEquals(RnCurveDecimation.of(RealScalar.of(1)).apply(tensor), tensor);
  }

  public void testPoints2same() {
    Tensor tensor = Tensors.matrix(new Number[][] { { 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 1 } });
    Tensor result = RnCurveDecimation.of(RealScalar.of(1)).apply(tensor);
    assertEquals(result, Tensors.fromString("{{1, 1}, {1, 1}}"));
  }

  public void testPoints3a() {
    Tensor tensor = Tensors.matrix(new Number[][] { { 1, 1 }, { 1, 1 }, { 5, 2 } });
    Tensor result = RnCurveDecimation.of(RealScalar.of(0)).apply(tensor);
    assertEquals(result, Tensors.fromString("{{1, 1}, {5, 2}}"));
  }

  public void testPoints3() {
    Tensor tensor = Tensors.matrix(new Number[][] { { 1, 1 }, { 3, 2 }, { 5, 2 } });
    assertEquals(RnCurveDecimation.of(RealScalar.of(1)).apply(tensor), //
        Tensors.matrixInt(new int[][] { { 1, 1 }, { 5, 2 } }));
    assertEquals(RnCurveDecimation.of(RealScalar.of(.1)).apply(tensor), tensor);
  }

  public void testRandom() {
    int n = 20;
    Tensor tensor = Tensors.vector(i -> Tensors.vector(i, RandomVariate.of(NormalDistribution.standard()).number().doubleValue()), n);
    Tensor result = RnCurveDecimation.of(RealScalar.of(1)).apply(tensor);
    Tensor column = result.get(Tensor.ALL, 0);
    assertEquals(column, Sort.of(column));
    assertTrue(column.length() < n);
  }

  public void testQuantity() {
    Tensor points = CirclePoints.of(100).map(value -> Quantity.of(value, "m"));
    Tensor tensor = RnCurveDecimation.of(Quantity.of(.03, "m")).apply(points);
    assertEquals(tensor.length(), 17);
  }

  public void test3d() {
    Tensor tensor = RnCurveDecimation.of(RealScalar.of(.1)).apply(IdentityMatrix.of(3));
    assertEquals(tensor, IdentityMatrix.of(3));
  }

  public void testEpsilonFail() {
    try {
      RnCurveDecimation.of(RealScalar.of(-.1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail() {
    try {
      RnCurveDecimation.of(RealScalar.of(.1)).apply(Tensors.fromString("{{{1}, 2}, {{1}, 2}, {{1}, 2}}"));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      RnCurveDecimation.of(RealScalar.of(.1)).apply(Array.zeros(3, 3, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      RnCurveDecimation.of(RealScalar.of(.1)).apply(Array.zeros(3, 2, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
