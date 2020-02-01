// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.io.Serializable;

import ch.ethz.idsc.owl.bot.rn.R1Integrator;
import ch.ethz.idsc.owl.bot.se2.Se2CarLieIntegrator;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;

/** exact integration of state for a period h during which a constant acceleration is assumed. */
public class Tse2Integrator implements Integrator, Serializable {
  private final Clip v_range;

  /** @param v_range */
  public Tse2Integrator(Clip v_range) {
    this.v_range = v_range;
  }

  @Override // from Integrator
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u_tse2, Scalar h) {
    // x = {px[m], py[m], theta[], vx[m*s^-1]}
    // u_tse2 = {rate[m^-1], ax[m*s^-2]}
    Scalar vx = x.Get(Tse2StateSpaceModel.STATE_INDEX_VEL);
    // Tensor u_tse2 = flow.getU();
    Scalar ax = u_tse2.Get(Tse2StateSpaceModel.CONTROL_INDEX_ACCEL);
    Tensor r1 = R1Integrator.direct(Tensors.of(vx.multiply(h).zero(), vx), ax, h);
    // movement along geodesic by distance dp
    Scalar dp = r1.Get(0);
    // difference da in orientation between time 0 and h
    // from m^-1 to []
    Scalar da = u_tse2.Get(Tse2StateSpaceModel.CONTROL_INDEX_STEER).multiply(dp);
    Tensor shift = Tensors.of(dp, dp.zero(), da);
    return Se2CarLieIntegrator.INSTANCE.spin(x.extract(0, 3), shift) //
        .append(v_range.apply(r1.Get(1)));
  }
}
