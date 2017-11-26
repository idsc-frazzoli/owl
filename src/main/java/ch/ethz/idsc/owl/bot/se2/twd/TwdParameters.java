// code by jl
package ch.ethz.idsc.owl.bot.se2.twd;

import ch.ethz.idsc.owl.glc.par.DefaultParameters;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Power;

/* package */ class TwdParameters extends DefaultParameters {
  public TwdParameters( //
      Scalar resolution, Scalar timeScale, Scalar depthScale, Tensor partitionScale, Scalar dtMax, int maxIter, //
      Scalar lipschitz) {
    super(resolution, timeScale, depthScale, partitionScale, dtMax, maxIter, lipschitz);
  }

  @Override
  /** @return if Lipschitz == 0: R*log(R)²/partitionScale */
  protected Tensor etaLfZero() {
    return getPartitionScale().map(Scalar::reciprocal) //
        .multiply(getResolution().multiply(Power.of(Log.of(getResolution()), 2)));
  }

  @Override
  /** @return R^(5/Pi)/partitionScale */
  // Formula from: B. Paden: A Generalized Label correcting Method: P. 57 Figure: 5-11
  protected Tensor etaLfNonZero(Scalar lipschitz) {
    return getPartitionScale().map(Scalar::reciprocal) //
        .multiply(Power.of(getResolution(), RealScalar.of(5).divide(RealScalar.of(Math.PI))));
  }
}
