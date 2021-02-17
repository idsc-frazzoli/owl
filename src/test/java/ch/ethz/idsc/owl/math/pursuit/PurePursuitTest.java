// code by jph
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Optional;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class PurePursuitTest extends TestCase {
  public void testRatioForwardLeftPositiveXUnit() {
    Tensor tensor = Tensors.fromString("{{.2[m], 0[m]}, {1[m], 1[m]}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, Quantity.of(1.0, "m"));
    Clip clip = Clips.interval(Quantity.of(1.2, "m^-1"), Quantity.of(1.5, "m^-1"));
    clip.requireInside(purePursuit.firstRatio().get());
  }

  public void testMatch2() {
    Tensor curve = Tensors.fromString("{{-0.4}, {0.6}, {1.4}, {2.2}}");
    AssertFail.of(() -> PurePursuit.fromTrajectory(curve, RealScalar.ONE));
  }

  public void testDistanceFail() {
    Tensor curve = Tensors.fromString("{{-0.4}, {0.6}, {1.4}, {2.2}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(curve, RealScalar.of(3.3));
    Optional<Tensor> optional = purePursuit.lookAhead();
    assertFalse(optional.isPresent());
  }

  public void testRatioFail() {
    Tensor tensor = Tensors.fromString("{{0.2, 0}, {1, 0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.5));
    Optional<Tensor> optional = purePursuit.lookAhead();
    assertFalse(optional.isPresent());
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  public void testEquals() {
    Tensor tensor = Tensors.fromString("{{1, 0}, {1, 0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.ONE);
    Optional<Tensor> optional = purePursuit.lookAhead();
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), UnitVector.of(2, 0));
    assertTrue(purePursuit.firstRatio().isPresent());
    assertEquals(purePursuit.firstRatio().get(), RealScalar.ZERO);
  }

  public void testEmpty() {
    Tensor tensor = Tensors.empty();
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.5));
    assertFalse(purePursuit.lookAhead().isPresent());
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  public void testDirectionFail() {
    Tensor tensor = Tensors.fromString("{{1, 1}, {0.3, 0.2}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.ONE);
    assertFalse(purePursuit.lookAhead().isPresent());
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  public void testRatioForward() {
    Tensor tensor = Tensors.fromString("{{0.2, 0}, {1, 0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Vector2Norm.NORMALIZE.apply(dir);
    Chop._12.requireClose(normal, Vector2Norm.NORMALIZE.apply(Tensors.vector(1, 0)));
    Optional<Scalar> optional = purePursuit.firstRatio();
    Scalar rate = optional.get();
    assertTrue(Scalars.isZero(rate));
  }

  public void testRatioForwardPositiveX() {
    Tensor tensor = Tensors.fromString("{{0.2, 0}, {1, 0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Scalar rate = purePursuit.firstRatio().get();
    assertTrue(Scalars.isZero(rate));
  }

  public void testRatioForwardLeftPositiveX() {
    Tensor tensor = Tensors.fromString("{{0.2, 0}, {1, 1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.0));
    Clip clip = Clips.interval(1.2, 1.5);
    clip.requireInside(purePursuit.firstRatio().get());
  }

  public void testRatioForwardRightPositiveX() {
    Tensor tensor = Tensors.fromString("{{0.2, 0}, {1, -1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.0));
    Clip clip = Clips.interval(-1.5, -1.2);
    clip.requireInside(purePursuit.firstRatio().get());
  }

  public void testRatioBackRight() {
    Tensor tensor = Tensors.fromString("{{0, 0}, {-1, -1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Vector2Norm.NORMALIZE.apply(dir);
    Chop._12.requireClose(normal, Vector2Norm.NORMALIZE.apply(Tensors.vector(-1, -1)));
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  public void testRatioBackLeft() {
    Tensor tensor = Tensors.fromString("{{0, 0}, {-1, 1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Vector2Norm.NORMALIZE.apply(dir);
    Chop._12.requireClose(normal, Vector2Norm.NORMALIZE.apply(Tensors.vector(-1, 1)));
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  public void testRatioXZero() {
    Tensor tensor = Tensors.fromString("{{0, 0.3}, {0, 1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Vector2Norm.NORMALIZE.apply(dir);
    Chop._12.requireClose(normal, Vector2Norm.NORMALIZE.apply(Tensors.vector(0, 1)));
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  // shows problem with non-positive x
  public void testRatioLarge() {
    Tensor tensor = Tensors.fromString("{{0, 0}, {-100, 1}}");
    Scalar distance = DoubleScalar.of(100.0);
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, distance);
    Optional<Scalar> optional = purePursuit.firstRatio();
    assertFalse(optional.isPresent());
  }
}
