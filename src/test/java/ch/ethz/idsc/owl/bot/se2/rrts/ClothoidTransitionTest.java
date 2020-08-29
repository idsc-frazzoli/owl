// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.IOException;

import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.sophus.clt.LagrangeQuadraticD;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class ClothoidTransitionTest extends TestCase {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();

  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor start = Tensors.vector(1, 2, 3);
    Tensor end = Tensors.vector(4, 1, 5);
    ClothoidTransition clothoidTransition = Serialization.copy(ClothoidTransition.of(CLOTHOID_BUILDER, start, end));
    LagrangeQuadraticD lagrangeQuadraticD = clothoidTransition.clothoid().curvature();
    Scalar head = lagrangeQuadraticD.head();
    Clips.interval(2.5, 2.6).requireInside(head);
  }

  public void testLog2Int() {
    int value = 1024;
    int bit = 31 - Integer.numberOfLeadingZeros(value);
    assertEquals(bit, 10);
  }

  public void testWrapped() {
    Tensor start = Tensors.vector(2, 3, 3);
    Tensor end = Tensors.vector(4, 1, 5);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    TransitionWrap transitionWrap = clothoidTransition.wrapped(RealScalar.of(0.2));
    assertEquals(transitionWrap.samples().length(), transitionWrap.spacing().length());
    assertTrue(transitionWrap.spacing().stream().map(Tensor::Get).allMatch(Sign::isPositive));
  }

  public void testSingularPoint() {
    Tensor start = Tensors.vector(0, 0, 0);
    Tensor end = Tensors.vector(0, 0, 0);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    assertEquals(clothoidTransition.linearized(RealScalar.of(0.1)), Array.zeros(2, 3));
  }

  public void testSamples2() {
    Tensor start = Tensors.vector(0, 0, 0);
    Tensor end = Tensors.vector(4, 0, 0);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    Chop._12.requireClose(clothoidTransition.length(), RealScalar.of(4));
    assertEquals(clothoidTransition.sampled(RealScalar.of(2.1)).length(), 2);
    assertEquals(clothoidTransition.sampled(RealScalar.of(1.9)).length(), 3);
  }

  public void testSamplesSteps() {
    Tensor start = Tensors.vector(1, 2, 3);
    Tensor end = Tensors.vector(4, 1, 5);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    assertEquals(clothoidTransition.sampled(RealScalar.of(0.2)).length(), 25);
    assertEquals(clothoidTransition.sampled(RealScalar.of(0.1)).length(), 50);
    assertEquals(clothoidTransition.linearized(RealScalar.of(0.2)).length(), 26);
    assertEquals(clothoidTransition.linearized(RealScalar.of(0.1)).length(), 51);
  }

  public void testFails() {
    Tensor start = Tensors.vector(1, 2, 3);
    Tensor end = Tensors.vector(4, 1, 5);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    try {
      clothoidTransition.sampled(RealScalar.of(-0.1));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      clothoidTransition.sampled(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      clothoidTransition.wrapped(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      clothoidTransition.linearized(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
