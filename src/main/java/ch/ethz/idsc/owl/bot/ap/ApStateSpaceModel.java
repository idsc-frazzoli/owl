//code by astoll
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.sca.Sin;

/** State-Space Model for Flying Aircraft
 * taken from "Validating a Hamilton-Jacobi Approximation to Hybrid System Reachable Sets⋆"
 * 
 * 
 * @author Andre
 * @param x = {velocity, flight path angle, horizontal distance, altitude}
 * @param u = {thrust, angle of attack} */
public enum ApStateSpaceModel implements StateSpaceModel {
  INSTANCE;
  // ---
  /** acceleration of gravity */
  private static final Scalar GRAVITY = RealScalar.of(9.81);
  /** total mass of airplane */
  private static final Scalar MASS = RealScalar.of(60_000);
  /** max thrust in N */
  public static final Scalar MAX_THRUST = RealScalar.of(160_000);
  /** max AOA in radian */
  public static final Scalar MAX_AOA = Degree.of(10);
  /** max speed */
  static final Scalar MAX_SPEED = RealScalar.of(83);

  @Override
  public Tensor f(Tensor x, Tensor u) {
    // x1' = 1/m * (u1*cos(u2) - D(u2,x1) - m*g*sin(x2))
    // x2' = 1/(m*x1) * (u1*sin(u2) + L(u2,x1) - m*g*cos(x2))
    // x3' = x1*sin(x2)
    // x4' = x1*cos(x2)
    // System.out.println("x=" + x);
    // System.out.println("u=" + u);
    Scalar x1 = x.Get(0); // velocity
    Scalar x2 = x.Get(1); // Flight path angle
    Scalar x3 = x.Get(2); // horizontal distance
    Scalar x4 = x.Get(3); // altitude
    Scalar u1 = u.Get(0); // Thrust
    Scalar u2 = u.Get(1); // angle of attack
    return Tensors.of(//
        u1.multiply(Cos.of(u2)).subtract(D(u2, x1)).subtract(Times.of(MASS, GRAVITY, Sin.of(x2))).divide(MASS), //
        u1.multiply(Sin.of(u2)).add(L(u2, x1)).subtract(Times.of(MASS, GRAVITY, Cos.of(x2))).divide(MASS).divide(x1), //
        x1.multiply(Cos.of(x2)), x1.multiply(Sin.of(x2)));
  }

  private static Scalar D(Scalar u2, Scalar x1) {
    double value = (1.25 + 4.2 * u2.number().doubleValue());
    return Times.of(RealScalar.of(2.7 + 3.08 * value * value), x1, x1);
  }

  private static Scalar L(Scalar u2, Scalar x1) {
    double value = (1.25 + 4.2 * u2.number().doubleValue());
    return Times.of(RealScalar.of(68.6 * value), x1, x1);
  }

  @Override
  public Scalar getLipschitz() {
    // TODO Auto-generated method stub
    return null;
  }
}
