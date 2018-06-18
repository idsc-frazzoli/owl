// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class PurePursuitTest extends TestCase {
  public void testMatch2() {
    Tensor curve = Tensors.fromString("{{-0.4},{0.6},{1.4},{2.2}}");
    try {
      PurePursuit.fromTrajectory(curve, RealScalar.ONE);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testDistanceFail() {
    Tensor curve = Tensors.fromString("{{-0.4},{0.6},{1.4},{2.2}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(curve, RealScalar.of(3.3));
    Optional<Tensor> optional = purePursuit.lookAhead();
    assertFalse(optional.isPresent());
  }

  public void testRatioFail() {
    Tensor tensor = Tensors.fromString("{{.2,0},{1,0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.5));
    Optional<Tensor> optional = purePursuit.lookAhead();
    assertFalse(optional.isPresent());
    assertFalse(purePursuit.ratio().isPresent());
  }

  public void testEquals() {
    Tensor tensor = Tensors.fromString("{{1,0},{1,0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.ONE);
    Optional<Tensor> optional = purePursuit.lookAhead();
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), UnitVector.of(2, 0));
    assertTrue(purePursuit.ratio().isPresent());
    assertEquals(purePursuit.ratio().get(), RealScalar.ZERO);
  }

  public void testEmpty() {
    Tensor tensor = Tensors.empty();
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.5));
    assertFalse(purePursuit.lookAhead().isPresent());
    assertFalse(purePursuit.ratio().isPresent());
  }

  public void testDirectionFail() {
    Tensor tensor = Tensors.fromString("{{1,1},{0.3,0.2}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.ONE);
    assertFalse(purePursuit.lookAhead().isPresent());
    assertFalse(purePursuit.ratio().isPresent());
  }

  public void testRatioForward() {
    Tensor tensor = Tensors.fromString("{{.2,0},{1,0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Normalize.of(dir);
    assertTrue(Chop._12.close(normal, Normalize.of(Tensors.vector(1, 0))));
    Optional<Scalar> optional = purePursuit.ratio();
    Scalar rate = optional.get();
    assertTrue(Scalars.isZero(rate));
  }

  public void testRatioForwardPositiveX() {
    Tensor tensor = Tensors.fromString("{{.2,0},{1,0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Scalar rate = purePursuit.ratio().get();
    assertTrue(Scalars.isZero(rate));
  }

  public void testRatioForwardLeftPositiveX() {
    Tensor tensor = Tensors.fromString("{{.2,0},{1,1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.0));
    Clip clip = Clip.function(1.2, 1.5);
    clip.requireInside(purePursuit.ratio().get());
  }

  public void testRatioForwardRightPositiveX() {
    Tensor tensor = Tensors.fromString("{{.2,0},{1,-1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.0));
    Clip clip = Clip.function(-1.5, -1.2);
    clip.requireInside(purePursuit.ratio().get());
  }

  public void testRatioBackRight() {
    Tensor tensor = Tensors.fromString("{{0,0},{-1,-1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Normalize.of(dir);
    assertTrue(Chop._12.close(normal, Normalize.of(Tensors.vector(-1, -1))));
    assertFalse(purePursuit.ratio().isPresent());
  }

  public void testRatioBackLeft() {
    Tensor tensor = Tensors.fromString("{{0,0},{-1,1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Normalize.of(dir);
    assertTrue(Chop._12.close(normal, Normalize.of(Tensors.vector(-1, 1))));
    assertFalse(purePursuit.ratio().isPresent());
  }

  public void testRatioXZero() {
    Tensor tensor = Tensors.fromString("{{0,.3},{0,1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Normalize.of(dir);
    assertTrue(Chop._12.close(normal, Normalize.of(Tensors.vector(0, 1))));
    assertFalse(purePursuit.ratio().isPresent());
  }

  // shows problem with non-positive x
  public void testRatioLarge() {
    Tensor tensor = Tensors.fromString("{{0,0},{-100,1}}");
    Scalar distance = DoubleScalar.of(100.0);
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, distance);
    Optional<Scalar> optional = purePursuit.ratio();
    assertFalse(optional.isPresent());
  }

  public void testRatioPositiveX() {
    for (Tensor lookAhead : Tensors.of(Tensors.vector(3, 1), Tensors.vector(3, -1))) {
      Optional<Scalar> optional = PurePursuit.ratioPositiveX(lookAhead);
      Scalar ratio = optional.get();
      Scalar speed = RealScalar.of(3.217506); // through experimentation
      Tensor u = Tensors.of(speed, RealScalar.ZERO, ratio.multiply(speed));
      Tensor tensor = Se2CarIntegrator.INSTANCE.spin(Array.zeros(3), u);
      assertTrue(Chop._06.close(tensor.extract(0, 2), lookAhead));
    }
  }

  public void testCreateLookAhead() {
    Distribution distribution = UniformDistribution.of(-0.3, +0.3);
    Distribution speeds = UniformDistribution.of(0, 3);
    for (Tensor _ratio : RandomVariate.of(distribution, 100)) {
      Scalar ratio = _ratio.Get();
      Scalar speed = RandomVariate.of(speeds);
      Tensor u = Tensors.of(speed, RealScalar.ZERO, ratio.multiply(speed));
      Tensor lookAhead = Se2CarIntegrator.INSTANCE.spin(Array.zeros(3), u);
      Optional<Scalar> optional = PurePursuit.ratioPositiveX(lookAhead);
      Scalar scalar = optional.get();
      assertTrue(Chop._07.close(ratio, scalar));
    }
  }

  public void testRatioNegativeX() {
    for (Tensor lookAhead : Tensors.of(Tensors.vector(-3, 1), Tensors.vector(-3, -1))) {
      Optional<Scalar> optional = PurePursuit.ratioNegativeX(lookAhead);
      Scalar ratio = optional.get();
      Scalar speed = RealScalar.of(-3.217506); // through experimentation
      Tensor u = Tensors.of(speed, RealScalar.ZERO, ratio.multiply(speed));
      Tensor tensor = Se2CarIntegrator.INSTANCE.spin(Array.zeros(3), u);
      assertTrue(Chop._06.close(tensor.extract(0, 2), lookAhead));
    }
  }
}
