// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.ethz.idsc.owl.math.dubins;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum DubinsPathType {
  LSR(+1, +0, -1, 1), //
  RSL(-1, +0, +1, 0), //
  LSL(+1, +0, +1, 3), //
  RSR(-1, +0, -1, 2), //
  LRL(+1, -1, +1, 5), //
  RLR(-1, +1, -1, 4), //
  ;
  private final Tensor scaSign;
  public final boolean LSR_or_RSL;
  public final boolean LSL_or_RSR;
  public final boolean LRL_or_RLR;
  private final boolean isFirstTurnRight;
  private final boolean isFirstEqualsLast;
  // ---
  private final int ordinal_inverse;

  private DubinsPathType(int s0s, int s1s, int s2s, int ordinal_inverse) {
    scaSign = Tensors.vector(s0s, s1s, s2s).unmodifiable();
    // middle segment is straight and segment 0 and 2 are different
    LSR_or_RSL = s1s == +0 && s0s != s2s;
    // middle segment is straight and segment 0 and 2 are identical
    LSL_or_RSR = s1s == +0 && s0s == s2s;
    // middle segment is not straight
    LRL_or_RLR = s1s != +0; //
    isFirstTurnRight = s0s == -1;
    isFirstEqualsLast = s0s == s2s;
    this.ordinal_inverse = ordinal_inverse;
  }

  public boolean isFirstTurnRight() {
    return isFirstTurnRight;
  }

  public boolean isFirstEqualsLast() {
    return isFirstEqualsLast;
  }

  public DubinsPathType inverse() {
    return values()[ordinal_inverse];
  }

  public Tensor tangent(int index, Scalar radius) {
    return Tensors.of(RealScalar.ONE, RealScalar.ZERO, scaSign.Get(index).divide(radius));
  }
}
