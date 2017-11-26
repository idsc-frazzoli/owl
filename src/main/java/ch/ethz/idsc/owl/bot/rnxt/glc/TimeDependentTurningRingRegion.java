// code by jl
package ch.ethz.idsc.owl.bot.rnxt.glc;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Mod;

/** evaluate does not correspond to Euclidean distance */
class TimeDependentTurningRingRegion implements Region<StateTime> {
  private static Mod MOD = Mod.function(2 * Math.PI, -Math.PI);
  // ---
  private final Tensor center;
  private final Scalar initialGapAngle;
  private final Scalar gapSizeAngle;
  private final Scalar lowerRingRadius;
  private final Scalar upperRingRadius;
  private final Scalar turningSpeed = Degree.of(30); // 30 °/s

  /** Constructs a Ring, with a gap in it, which turns at 30°/s CCW
   * 
   * @param center vector of length 2
   * @param initialGapAngle: initial position where Gap should be
   * @param gapSizeAngle: size of Gap in rad
   * @param ringThickness: thickness of the obstacleRing
   * @param ringRadius: Radius of the Ring (to the middle) */
  public TimeDependentTurningRingRegion(Tensor center, Scalar initialGapAngle, Scalar gapSizeAngle, Scalar ringThickness, Scalar ringRadius) {
    GlobalAssert.that(VectorQ.ofLength(center, 2));
    this.center = center;
    this.initialGapAngle = initialGapAngle;
    this.gapSizeAngle = gapSizeAngle;
    lowerRingRadius = ringRadius.subtract(ringThickness.divide(RealScalar.of(2)));
    upperRingRadius = ringRadius.add(ringThickness.divide(RealScalar.of(2)));
  }

  @Override
  public boolean isMember(StateTime stateTime) {
    Tensor state = stateTime.state();
    GlobalAssert.that(VectorQ.ofLength(state, 2));
    Scalar time = stateTime.time();
    Scalar radius = Norm._2.between(state, center);
    if (Scalars.lessEquals(lowerRingRadius, radius) && Scalars.lessEquals(radius, upperRingRadius)) { // in Obstacle radial
      Scalar upperGapAngle = initialGapAngle.add(gapSizeAngle.divide(RealScalar.of(2)));
      Scalar lowerGapAngle = initialGapAngle.subtract(gapSizeAngle.divide(RealScalar.of(2)));
      Tensor vec1 = state.subtract(center);
      Scalar angle = ArcTan.of(vec1.Get(0), vec1.Get(1)).subtract(turningSpeed.multiply(time));
      // checks if in Gap, Otherwise in Ring
      return !(Scalars.lessEquals(MOD.of(angle), upperGapAngle) && Scalars.lessEquals(lowerGapAngle, MOD.of(angle)));
    }
    return false;
  }
}
