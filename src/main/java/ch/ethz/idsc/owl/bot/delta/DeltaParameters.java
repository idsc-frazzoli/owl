// code by jl
package ch.ethz.idsc.owl.bot.delta;

import ch.ethz.idsc.owl.glc.par.DefaultParameters;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Power;

public class DeltaParameters extends DefaultParameters {
  public DeltaParameters( //
      Scalar resolution, Scalar timeScale, Scalar depthScale, Tensor partitionScale, Scalar dtMax, int maxIter, //
      Scalar lipschitz) {
    super(resolution, timeScale, depthScale, partitionScale, dtMax, maxIter, lipschitz);
  }

  /** @return if Lipschitz == 0: R²/partitionScale */
  @Override
  protected final Tensor etaLfZero() {
    return getPartitionScale().map(Scalar::reciprocal).multiply(Power.of(getResolution(), 2)); //
  }

  /** @return R^(1+Lf)/partitionScale */
  @Override
  protected final Tensor etaLfNonZero(Scalar lipschitz) {
    return getPartitionScale().map(Scalar::reciprocal) //
        .multiply(Power.of(getResolution(), RealScalar.ONE.add(lipschitz)));
  }
}
