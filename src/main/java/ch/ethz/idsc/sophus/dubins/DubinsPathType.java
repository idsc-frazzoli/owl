// code by jph
package ch.ethz.idsc.sophus.dubins;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

public enum DubinsPathType {
  LSR(+1, +0, -1, Steer2TurnsDiffSide.INSTANCE), //
  RSL(-1, +0, +1, Steer2TurnsDiffSide.INSTANCE), //
  LSL(+1, +0, +1, Steer2TurnsSameSide.INSTANCE), //
  RSR(-1, +0, -1, Steer2TurnsSameSide.INSTANCE), //
  LRL(+1, -1, +1, Steer3Turns.INSTANCE), //
  RLR(-1, +1, -1, Steer3Turns.INSTANCE), //
  ;
  // ---
  private final Tensor signature;
  private final Tensor signatureAbs;
  private final boolean isFirstTurnRight;
  private final boolean isFirstEqualsLast;
  private final DubinsSteer dubinsSteer;

  private DubinsPathType(int s0s, int s1s, int s2s, DubinsSteer dubinsSteer) {
    signature = Tensors.vector(s0s, s1s, s2s).unmodifiable();
    signatureAbs = signature.map(Scalar::abs).unmodifiable();
    isFirstTurnRight = s0s == -1;
    isFirstEqualsLast = s0s == s2s;
    this.dubinsSteer = dubinsSteer;
  }

  /** @return true if type is RSL or RSR or RLR */
  public boolean isFirstTurnRight() {
    return isFirstTurnRight;
  }

  /** @return true if type is LSL or RSR or LRL or RLR */
  public boolean isFirstEqualsLast() {
    return isFirstEqualsLast;
  }

  public Tensor signatureAbs() {
    return signatureAbs;
  }

  /* package */ DubinsSteer dubinsSteer() {
    return dubinsSteer;
  }

  /** @param index 0, 1, or 2
   * @param radius positive
   * @return vector with first and second entry unitless.
   * result is multiplied with length of segment */
  /* package */ Tensor tangent(int index, Scalar radius) {
    return Tensors.of(RealScalar.ONE, RealScalar.ZERO, //
        signature.Get(index).divide(Sign.requirePositive(radius)));
  }
}
