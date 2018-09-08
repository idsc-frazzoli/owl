// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.owl.bot.rn.R1Integrator;
import ch.ethz.idsc.owl.bot.se2.Se2CarLieIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** should be exact */
/* package */ enum Tse2Integrator implements Integrator {
  INSTANCE;
  // ---
  @Override
  public Tensor step(Flow flow, Tensor x, Scalar h) {
    // x = {px[m], py[m], theta[rad], vx[m*s^-1]}
    // u_tse2 = {rate[rad*m^-1], ax[m*s^-2]}
    Scalar vx = x.Get(Tse2StateSpaceModel.STATE_INDEX_VEL);
    Tensor u_tse2 = flow.getU();
    Scalar ax = u_tse2.Get(Tse2StateSpaceModel.CONTROL_INDEX_ACCEL);
    Tensor r1 = R1Integrator.direct(Tensors.of(vx.multiply(h).zero(), vx), ax, h);
    // movement along geodesic by distance dp
    Scalar dp = r1.Get(0);
    // difference da in orientation between time 0 and h
    // from rad*m^-1 to rad
    Scalar da = u_tse2.Get(Tse2StateSpaceModel.CONTROL_INDEX_STEER).multiply(dp);
    Tensor shift = Tensors.of(dp, dp.zero(), da);
    return Se2CarLieIntegrator.INSTANCE.spin(x.extract(0, 3), shift).append(r1.get(1));
  }
}
