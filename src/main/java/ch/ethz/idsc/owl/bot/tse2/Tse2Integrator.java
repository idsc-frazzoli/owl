// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.owl.bot.se2.Se2CarLieIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

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
    Scalar or = u_tse2.Get(Tse2StateSpaceModel.CONTROL_INDEX_STEER).multiply(vx); // from rad*m^-1 to rad*s^-1
    Tensor u_se2 = Tensors.of(vx, vx.zero(), or);
    return Se2CarLieIntegrator.INSTANCE.spin(x.extract(0, 3), u_se2.multiply(h)) //
        .append(vx.add(ax.multiply(h)));
  }
}
