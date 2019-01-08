// code by jph
package ch.ethz.idsc.owl.bot.sat;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.red.Norm;

class SatelliteStateSpaceModel implements StateSpaceModel, Serializable {
  /** @param x of the form {px, py, vx, vy}
   * @param u of the form {ux, uy} */
  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    Tensor pos = Extract2D.FUNCTION.apply(x);
    Tensor vel = x.extract(2, 4);
    Tensor acc = pos.multiply(Norm._2.ofVector(pos).negate());
    return Join.of(vel, acc.add(u));
  }

  @Override // from StateSpaceModel
  public Scalar getLipschitz() {
    return RealScalar.ONE;
  }
}
