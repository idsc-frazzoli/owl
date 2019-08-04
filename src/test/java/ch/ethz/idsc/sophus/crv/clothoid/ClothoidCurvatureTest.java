// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import java.io.IOException;

import ch.ethz.idsc.sophus.math.HeadTailInterface;
import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClothoidCurvatureTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor p = Tensors.vector(1, 2, 1);
    Tensor q = Tensors.vector(8, 6, 2);
    HeadTailInterface clothoidTerminalRatio = ClothoidTerminalRatios.planar(p, q);
    Scalar head = clothoidTerminalRatio.head();
    ClothoidCurvature clothoidCurvature = Serialization.copy(new ClothoidCurvature(p, q));
    Scalar scalar = clothoidCurvature.head();
    Chop._01.requireClose(head, scalar);
  }

  public void testStraight() {
    Tensor p = Tensors.vector(1, 2, 0);
    Tensor q = Tensors.vector(10, 2, 0);
    HeadTailInterface clothoidTerminalRatio = ClothoidTerminalRatios.of(p, q);
    Chop._12.requireClose(clothoidTerminalRatio.head(), RealScalar.ZERO);
    Chop._12.requireClose(clothoidTerminalRatio.tail(), RealScalar.ZERO);
    ClothoidCurvature clothoidCurvature = new ClothoidCurvature(p, q);
    Chop._12.requireClose(clothoidCurvature.head(), RealScalar.ZERO);
    Chop._12.requireClose(clothoidCurvature.tail(), RealScalar.ZERO);
  }

  public void testAlmostStraight() {
    Tensor p = Tensors.vector(1, 2, 0);
    Tensor q = Tensors.vector(10, 3, 0);
    HeadTailInterface clothoidTerminalRatio = ClothoidTerminalRatios.planar(p, q);
    Scalar head = clothoidTerminalRatio.head();
    ClothoidCurvature clothoidCurvature = new ClothoidCurvature(p, q);
    Scalar scalar = clothoidCurvature.apply(RealScalar.ZERO);
    Chop._12.requireClose(clothoidCurvature.head(), scalar);
    Chop._02.requireClose(head, scalar);
  }

  public void testSingular() {
    Tensor p = Tensors.vector(1, 2, 1);
    Tensor q = Tensors.vector(1, 2, 1);
    ClothoidCurvature clothoidCurvature = new ClothoidCurvature(p, q);
    Scalar head = clothoidCurvature.head();
    Scalar tail = clothoidCurvature.tail();
    assertFalse(NumberQ.of(head));
    assertFalse(NumberQ.of(tail));
  }
}
