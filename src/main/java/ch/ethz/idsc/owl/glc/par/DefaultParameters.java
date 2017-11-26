// code by jl
package ch.ethz.idsc.owl.glc.par;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public abstract class DefaultParameters extends Parameters {
  private final Scalar lipschitz;

  public DefaultParameters( //
      Scalar resolution, Scalar timeScale, Scalar depthScale, //
      Tensor partitionScale, Scalar dtMax, int maxIter, Scalar lipschitz) {
    super(resolution, timeScale, depthScale, partitionScale, dtMax, maxIter);
    this.lipschitz = lipschitz;
  }

  @Override
  /** @return if Lipschitz == 0: R² / PS
   * @return else : R^(1+Lipschitz) /PS */
  public Tensor getEta() {
    if (Scalars.isZero(lipschitz))
      return etaLfZero();
    return etaLfNonZero(lipschitz);
  }

  protected abstract Tensor etaLfZero();

  protected abstract Tensor etaLfNonZero(Scalar lipschitz);
}
