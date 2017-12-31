// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Optional;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class PurePursuitTest extends TestCase {
  public void testMatch2() {
    Tensor curve = Tensors.fromString("{{-0.4},{0.6},{1.4},{2.2}}");
    Optional<Tensor> optional = PurePursuit.beacon(curve, RealScalar.ONE);
    assertTrue(optional.isPresent());
    Tensor point = optional.get();
    assertEquals(point, Tensors.vector(1));
  }

  public void testDistanceFail() {
    Tensor curve = Tensors.fromString("{{-0.4},{0.6},{1.4},{2.2}}");
    Optional<Tensor> optional = PurePursuit.beacon(curve, RealScalar.of(3.3));
    assertFalse(optional.isPresent());
  }

  public void testRatioFail() {
    Tensor tensor = Tensors.fromString("{{.2,0},{1,0}}");
    Optional<Tensor> optional = PurePursuit.beacon(tensor, RealScalar.of(1.5));
    assertFalse(optional.isPresent());
    assertFalse(PurePursuit.turningRatePositiveX(tensor, RealScalar.of(1.5)).isPresent());
  }

  public void testEquals() {
    Tensor tensor = Tensors.fromString("{{1,0},{1,0}}");
    {
      Optional<Tensor> optional = PurePursuit.beacon(tensor, RealScalar.ONE);
      assertTrue(optional.isPresent());
      assertEquals(optional.get(), UnitVector.of(2, 0));
    }
    Optional<Scalar> optional = PurePursuit.turningRatePositiveX(tensor, RealScalar.of(1));
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), RealScalar.ZERO);
  }

  public void testEmpty() {
    Tensor tensor = Tensors.empty();
    Optional<Tensor> optional = PurePursuit.beacon(tensor, RealScalar.of(1.5));
    assertFalse(optional.isPresent());
    assertFalse(PurePursuit.turningRatePositiveX(tensor, RealScalar.of(1.5)).isPresent());
  }

  public void testDirectionFail() {
    Tensor tensor = Tensors.fromString("{{1,1},{0.3,0.2}}");
    Optional<Tensor> optional = PurePursuit.beacon(tensor, RealScalar.ONE);
    assertFalse(optional.isPresent());
    assertFalse(PurePursuit.turningRatePositiveX(tensor, RealScalar.ONE).isPresent());
  }

  public void testRatioForward() {
    Tensor tensor = Tensors.fromString("{{.2,0},{1,0}}");
    {
      Optional<Tensor> optional = PurePursuit.beacon(tensor, RealScalar.of(0.5));
      Tensor dir = optional.get();
      Tensor normal = Normalize.of(dir);
      assertTrue(Chop._12.close(normal, Normalize.of(Tensors.vector(1, 0))));
    }
    Optional<Scalar> optional = PurePursuit.turningRatePositiveX(tensor, RealScalar.of(0.5));
    Scalar rate = optional.get();
    assertTrue(Scalars.isZero(rate));
  }

  public void testRatioForwardPositiveX() {
    Tensor tensor = Tensors.fromString("{{.2,0},{1,0}}");
    Optional<Scalar> optional = PurePursuit.turningRatePositiveX(tensor, RealScalar.of(0.5));
    Scalar rate = optional.get();
    assertTrue(Scalars.isZero(rate));
  }

  public void testRatioForwardLeftPositiveX() {
    Tensor tensor = Tensors.fromString("{{.2,0},{1,1}}");
    Optional<Scalar> optional = PurePursuit.turningRatePositiveX(tensor, RealScalar.of(1.0));
    Scalar rate = optional.get();
    Clip clip = Clip.function(1.2, 1.5);
    clip.isInsideElseThrow(rate);
  }

  public void testRatioForwardRightPositiveX() {
    Tensor tensor = Tensors.fromString("{{.2,0},{1,-1}}");
    Optional<Scalar> optional = PurePursuit.turningRatePositiveX(tensor, RealScalar.of(1.0));
    Scalar rate = optional.get();
    Clip clip = Clip.function(-1.5, -1.2);
    clip.isInsideElseThrow(rate);
  }

  public void testRatioBackRight() {
    Tensor tensor = Tensors.fromString("{{0,0},{-1,-1}}");
    {
      Optional<Tensor> optional = PurePursuit.beacon(tensor, RealScalar.of(0.5));
      Tensor dir = optional.get();
      Tensor normal = Normalize.of(dir);
      assertTrue(Chop._12.close(normal, Normalize.of(Tensors.vector(-1, -1))));
    }
    Optional<Scalar> optional = PurePursuit.turningRatePositiveX(tensor, RealScalar.of(0.5));
    assertFalse(optional.isPresent());
  }

  public void testRatioBackLeft() {
    Tensor tensor = Tensors.fromString("{{0,0},{-1,1}}");
    {
      Optional<Tensor> optional = PurePursuit.beacon(tensor, RealScalar.of(0.5));
      Tensor dir = optional.get();
      Tensor normal = Normalize.of(dir);
      assertTrue(Chop._12.close(normal, Normalize.of(Tensors.vector(-1, 1))));
    }
    Optional<Scalar> optional = PurePursuit.turningRatePositiveX(tensor, RealScalar.of(0.5));
    assertFalse(optional.isPresent());
  }

  public void testRatioXZero() {
    Tensor tensor = Tensors.fromString("{{0,.3},{0,1}}");
    {
      Optional<Tensor> optional = PurePursuit.beacon(tensor, RealScalar.of(0.5));
      Tensor dir = optional.get();
      Tensor normal = Normalize.of(dir);
      assertTrue(Chop._12.close(normal, Normalize.of(Tensors.vector(0, 1))));
    }
    Optional<Scalar> optional = PurePursuit.turningRatePositiveX(tensor, RealScalar.of(0.5));
    assertFalse(optional.isPresent());
  }

  // shows problem with non-positive x
  @SuppressWarnings("unused")
  public void testRatioLarge() {
    Tensor tensor = Tensors.fromString("{{0,0},{-100,1}}");
    Scalar distance = DoubleScalar.of(100.0);
    {
      Optional<Tensor> optional = PurePursuit.beacon(tensor, distance);
      Tensor dir = optional.get();
      // System.out.println(dir);
      // Tensor normal = Normalize.of(dir);
      // assertTrue(Chop._12.close(normal, Normalize.of(Tensors.vector(0, 1))));
    }
    Optional<Scalar> optional = PurePursuit.turningRatePositiveX(tensor, distance);
    assertFalse(optional.isPresent());
  }
}
