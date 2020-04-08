// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.io.IOException;
import java.util.Optional;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClothoidPursuitTest extends TestCase {
  public void testPointRadius1() throws ClassNotFoundException, IOException {
    PursuitInterface pursuitInterface = //
        Serialization.copy(ClothoidPursuit.of(Tensors.vector(1, 1, Math.PI / 2)));
    Optional<Scalar> optional = pursuitInterface.firstRatio();
    Chop._03.requireClose(optional.get(), RealScalar.ONE);
  }

  public void testPointRadius1Neg() {
    PursuitInterface pursuitInterface = ClothoidPursuit.of(Tensors.vector(1, -1, -Math.PI / 2));
    Optional<Scalar> optional = pursuitInterface.firstRatio();
    Chop._03.requireClose(optional.get(), RealScalar.ONE.negate());
  }

  public void testPointRadiusTwo() {
    PursuitInterface pursuitInterface = ClothoidPursuit.of(Tensors.vector(2, 2, Math.PI / 2));
    Optional<Scalar> optional = pursuitInterface.firstRatio();
    Chop._03.requireClose(optional.get(), RationalScalar.HALF);
  }

  public void testPointRadiusTwoNeg() {
    PursuitInterface pursuitInterface = ClothoidPursuit.of(Tensors.vector(2, -2, -Math.PI / 2));
    Optional<Scalar> optional = pursuitInterface.firstRatio();
    Chop._03.requireClose(optional.get(), RationalScalar.HALF.negate());
  }

  public void testPointRadiusStraight() {
    PursuitInterface pursuitInterface = ClothoidPursuit.of(Tensors.vector(10, 0, 0));
    Optional<Scalar> optional = pursuitInterface.firstRatio();
    Chop._12.requireClose(optional.get(), RealScalar.ZERO);
  }

  public void testQuantity() {
    PursuitInterface pursuitInterface = ClothoidPursuit.of(Tensors.fromString("{1[m], 1[m], 0.3}"));
    Optional<Scalar> optional = pursuitInterface.firstRatio();
    Clips.interval(Quantity.of(2.765, "m^-1"), Quantity.of(2.79, "m^-1")).requireInside(optional.get());
  }
}
