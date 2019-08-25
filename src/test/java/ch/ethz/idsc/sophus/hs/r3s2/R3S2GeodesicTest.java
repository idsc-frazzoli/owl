// code by jph
package ch.ethz.idsc.sophus.hs.r3s2;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class R3S2GeodesicTest extends TestCase {
  public void testZero() {
    Tensor split = R3S2Geodesic.INSTANCE.split( //
        Tensors.fromString("{{1, 2, 3}, {1, 0, 0}}"), //
        Tensors.fromString("{{8, 8, 8}, {0, 1, 0}}"), //
        RealScalar.ZERO);
    assertEquals(split, Tensors.fromString("{{1, 2, 3}, {1, 0, 0}}"));
  }

  public void testCurve() {
    Tensor p = Tensors.fromString("{{1, 2, 3}, {1, 0, 0}}");
    Tensor q = Tensors.fromString("{{8, 8, 8}, {0, 1, 0}}");
    ScalarTensorFunction curve = R3S2Geodesic.INSTANCE.curve(p, q);
    Tensor domain = Subdivide.of(0, 1, 12);
    Tensor result = domain.map(curve);
    Tensor splits = domain.map(t -> R3S2Geodesic.INSTANCE.split(p, q, t));
    Chop._13.requireClose(result, splits);
  }

  public void testOne() {
    Tensor split = R3S2Geodesic.INSTANCE.split( //
        Tensors.fromString("{{1, 2, 3}, {1, 0, 0}}"), //
        Tensors.fromString("{{8, 8, 8}, {0, 1, 0}}"), //
        RealScalar.ONE);
    assertTrue(Chop._10.close(split, Tensors.fromString("{{8, 8, 8}, {0, 1, 0}}")));
  }

  public void testHalfShift() {
    Tensor split = R3S2Geodesic.INSTANCE.split( //
        Tensors.fromString("{{1, 2, 3}, {1, 0, 0}}"), //
        Tensors.fromString("{{8, 8, 8}, {1, 0, 0}}"), //
        RationalScalar.HALF);
    assertTrue(Chop._10.close(split, Tensors.fromString("{{4.5, 5, 5.5}, {1, 0, 0}}")));
  }

  public void testHalfRotate() {
    Tensor split = R3S2Geodesic.INSTANCE.split( //
        Tensors.fromString("{{1, 2, 3}, {1, 0, 0}}"), //
        Tensors.fromString("{{1, 2, 3}, {0, 1, 0}}"), //
        RationalScalar.HALF);
    assertTrue(Chop._10.close(split, Tensors.fromString( //
        "{{1, 2, 3}, {0.7071067811865476, 0.7071067811865475, 0}}")));
  }

  public void testHalfSome() {
    Tensor split = R3S2Geodesic.INSTANCE.split( //
        Tensors.fromString("{{1, 2, 3}, {1, 0, 0}}"), //
        Tensors.fromString("{{8, 8, 8}, {0, 1, 0}}"), //
        RationalScalar.HALF);
    assertTrue(Chop._10.close(split.get(0), Tensors.fromString( //
        "{5.742640687119285, 3.5502525316941673, 5.5}")));
    assertTrue(Chop._10.close(split.get(1), Tensors.fromString( //
        "{0.7071067811865476, 0.7071067811865475, 0}")));
  }

  public void testSphereRepro() {
    Tensor n0 = UnitVector.of(3, 0);
    Tensor n1 = UnitVector.of(3, 1);
    Tensor p = Tensors.of(n0, n0);
    Tensor q = Tensors.of(n1, n1);
    Tensor split = R3S2Geodesic.INSTANCE.split(p, q, RationalScalar.HALF);
    assertTrue(Chop._12.close(split.get(0), split.get(1)));
  }
}
