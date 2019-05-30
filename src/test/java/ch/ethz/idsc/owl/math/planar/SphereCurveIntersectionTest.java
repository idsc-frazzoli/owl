// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.RotateLeft;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SphereCurveIntersectionTest extends TestCase {
  public void testString() {
    Tensor curve = Tensors.fromString("{{0,0},{1,0},{1,1},{0,1}}").unmodifiable();
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.string(RotateLeft.of(curve, index));
      assertEquals(optional.isPresent(), index != 1);
      if (index != 1) {
        Tensor tensor = optional.get();
        ExactTensorQ.require(tensor);
        assertEquals(tensor, Tensors.vector(0.5, 0));
      }
    }
  }

  public void testCyclic() {
    Tensor curve = Tensors.fromString("{{0,0},{1,0},{1,1},{0,1}}").unmodifiable();
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.cyclic(RotateLeft.of(curve, index));
      assertTrue(optional.isPresent());
      Tensor tensor = optional.get();
      ExactTensorQ.require(tensor);
      assertEquals(tensor, Tensors.vector(0.5, 0));
    }
  }

  public void testQuantity() {
    Tensor curve = Tensors.fromString("{{0[m],0[m]},{1[m],0[m]},{1[m],1[m]},{0[m],1[m]}}").unmodifiable();
    CurveIntersection curveIntersection = new SphereCurveIntersection(Quantity.of(RationalScalar.HALF, "m"));
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.cyclic(RotateLeft.of(curve, index));
      assertTrue(optional.isPresent());
      Tensor tensor = optional.get();
      ExactTensorQ.require(tensor);
      assertEquals(tensor, Tensors.fromString("{1/2[m],0[m]}"));
    }
  }

  public void testPoint() {
    Tensor curve = Tensors.fromString("{{1,0}}").unmodifiable();
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    assertFalse(curveIntersection.cyclic(curve).isPresent());
    assertFalse(curveIntersection.string(curve).isPresent());
  }

  public void testEmpty() {
    Tensor curve = Tensors.empty().unmodifiable();
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    assertFalse(curveIntersection.cyclic(curve).isPresent());
    assertFalse(curveIntersection.string(curve).isPresent());
  }

  public void testOne() {
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    Tensor curve = Tensors.fromString("{{-1},{0},{1},{2},{3}}").unmodifiable();
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.cyclic(RotateLeft.of(curve, index));
      assertTrue(optional.isPresent());
      Tensor tensor = optional.get();
      ExactTensorQ.require(tensor);
      assertEquals(tensor, Tensors.of(RationalScalar.HALF));
    }
  }

  public void testThree() {
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    Tensor curve = Tensors.fromString("{{0,0,0},{1,0,0},{1,1,0},{0,1,0}}").unmodifiable();
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.cyclic(RotateLeft.of(curve, index));
      assertTrue(optional.isPresent());
      Tensor tensor = optional.get();
      ExactTensorQ.require(tensor);
      assertEquals(tensor, Tensors.vector(0.5, 0, 0));
    }
  }

  public void testAssisted() {
    Tensor base = Tensors.vector(1, 0, 0);
    Tensor curve = Tensor.of(IntStream.range(0, 500).mapToObj(RealScalar::of).map(base::multiply)).unmodifiable();
    {
      // Timing timing = Timing.started();
      for (int index = 0; index < curve.length() - 1; index += 5) {
        Scalar radius = RealScalar.of(index + 1.5);
        AssistedCurveIntersection curveIntersection = new SphereCurveIntersection(radius);
        Optional<Tensor> optional = curveIntersection.string(curve);
        assertEquals(base.multiply(radius), optional.get());
      }
      // timing.stop();
      // System.out.println(timing.seconds());
    }
    {
      // Timing timing = Timing.started();
      int prevIdx = 0;
      for (int index = 0; index < curve.length() - 1; index += 5) {
        Scalar radius = RealScalar.of(index + 1.5);
        AssistedCurveIntersection curveIntersection = new SphereCurveIntersection(radius);
        Optional<CurvePoint> optional = curveIntersection.string(curve, prevIdx);
        prevIdx = optional.get().getIndex();
        assertEquals(radius.number().intValue(), prevIdx);
        assertEquals(base.multiply(radius), optional.get().getTensor());
      }
      // timing.stop();
      // System.out.println(timing.seconds());
    }
  }
}
