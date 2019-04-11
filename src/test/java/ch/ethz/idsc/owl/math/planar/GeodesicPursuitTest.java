// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Round;
import junit.framework.TestCase;

public class GeodesicPursuitTest extends TestCase {
  public void testSimple() {
    GeodesicPursuitInterface geodesicPursuit;
    Tensor trajectory1 = Tensors.of( //
        Tensors.vector(0, 0, 0), //
        Tensors.vector(2, 2, Math.PI / 2), //
        Tensors.vector(4, 4, Math.PI / 2));
    Tensor trajectory2 = Tensors.of( //
        Tensors.vector(2, 2, Math.PI / 2), //
        Tensors.vector(4, 4, Math.PI / 2));
    // ---
    geodesicPursuit = GeodesicPursuit.fromTrajectory(ClothoidCurve.INSTANCE, trajectory1, new NaiveEntryFinder(0), RealScalar.ONE);
    // System.out.println("ratios 1 = " + (geodesicPursuit.firstRatio().isPresent() ? geodesicPursuit.firstRatio().get() : "empty"));
    assertEquals(RationalScalar.of(1, 2), Round._8.apply(geodesicPursuit.firstRatio().orElse(null)));
    // ---
    geodesicPursuit = GeodesicPursuit.fromTrajectory(ClothoidCurve.INSTANCE, trajectory2, new NaiveEntryFinder(0));
    // System.out.println("ratios 2 = " + (geodesicPursuit.firstRatio().isPresent() ? geodesicPursuit.firstRatio().get() : "empty"));
    assertEquals(RationalScalar.of(1, 2), Round._8.apply(geodesicPursuit.firstRatio().orElse(null)));
  }

  public void testPointRadius1() {
    GeodesicPursuitInterface geodesicPursuit = new GeodesicPursuit(ClothoidCurve.INSTANCE, Tensors.vector(1, 1, Math.PI / 2));
    Optional<Scalar> optional = geodesicPursuit.firstRatio();
    Chop._12.close(optional.get(), RealScalar.ONE);
  }

  public void testPointRadius1Neg() {
    GeodesicPursuitInterface geodesicPursuit = new GeodesicPursuit(ClothoidCurve.INSTANCE, Tensors.vector(1, -1, -Math.PI / 2));
    Optional<Scalar> optional = geodesicPursuit.firstRatio();
    Chop._12.close(optional.get(), RealScalar.ONE.negate());
  }

  public void testPointRadiusTwo() {
    GeodesicPursuitInterface geodesicPursuit = new GeodesicPursuit(ClothoidCurve.INSTANCE, Tensors.vector(2, 2, Math.PI / 2));
    Optional<Scalar> optional = geodesicPursuit.firstRatio();
    Chop._12.close(optional.get(), RationalScalar.HALF);
  }

  public void testPointRadiusTwoNeg() {
    GeodesicPursuitInterface geodesicPursuit = new GeodesicPursuit(ClothoidCurve.INSTANCE, Tensors.vector(2, -2, -Math.PI / 2));
    Optional<Scalar> optional = geodesicPursuit.firstRatio();
    Chop._12.close(optional.get(), RationalScalar.HALF.negate());
  }

  public void testPointRadiusStraight() {
    GeodesicPursuitInterface geodesicPursuit = new GeodesicPursuit(ClothoidCurve.INSTANCE, Tensors.vector(10, 0, 0));
    Optional<Scalar> optional = geodesicPursuit.firstRatio();
    Chop._12.close(optional.get(), RealScalar.ZERO);
  }
}
