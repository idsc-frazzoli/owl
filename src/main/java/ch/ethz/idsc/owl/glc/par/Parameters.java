// code by jl, theory by bp
package ch.ethz.idsc.owl.glc.par;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Log;

public abstract class Parameters {
  // Discretization resolution
  private final Scalar resolution;
  // Maximum iterations
  private final int maxIter;
  // Initial partition size
  private final Tensor partitionScale;
  // integration step
  private final Scalar dtMax;
  // Time between nodes
  private final Scalar expandTime;
  // depth limit
  private Scalar depthLimit;

  /** @param resolution: resolution of algorithm, has to be an integer, i.e. satisfy IntegerQ::of
   * @param timeScale: Change time coordinate to be appropriate
   * @param depthScale: Adjust initial depth Limit
   * @param partitionScale: Initial Partition Scale
   * @param dtMax: Maximum integrationstep size
   * @param maxIter: maximum iterations */
  public Parameters( //
      Scalar resolution, Scalar timeScale, Scalar depthScale, Tensor partitionScale, Scalar dtMax, int maxIter) {
    // resolution needs to be a Integer as of A Generalized Label Correcting Algorithm, p.35, B. Paden
    // The input space is indexed by the resolution
    int intResolution = Scalars.intValueExact(resolution);
    if (intResolution <= 0)
      throw TensorRuntimeException.of(resolution);
    this.resolution = resolution;
    this.partitionScale = partitionScale;
    this.dtMax = dtMax;
    this.maxIter = maxIter;
    this.expandTime = timeScale.divide(resolution);
    this.depthLimit = depthScale //
        .multiply(resolution) //
        .multiply(Log.of(resolution));
  }

  /** @return time_scale / Resolution */
  public Scalar getdtMax() {
    return dtMax;
  }

  /** @return depthScale * R * log(R) */
  public Scalar getDepthLimitExact() {
    return depthLimit;
  }

  /** @return expandTime = timeScale/R */
  public Scalar getExpandTime() {
    return expandTime;
  }

  public int getDepthLimit() {
    return depthLimit.number().intValue();
  }

  /** @param increment value by which to increase the depthlimit */
  public final void increaseDepthLimit(int increment) {
    depthLimit = depthLimit.add(RealScalar.of(increment));
  }

  /** @param Lipschitz
   * ETA = 1/domainSize
   * @return if (Lipschitz ==0) R * log(R)²/partitionScale
   * @return else: R^(1+Lipschitz)/partitionScale */
  public abstract Tensor getEta();

  /** @return trajectory size with current expandTime and dtMax */
  public final int getTrajectorySize() {
    Scalar temp = Ceiling.of(expandTime.divide(dtMax));
    return temp.number().intValue();
  }

  public final int getmaxIter() {
    return maxIter;
  }

  public final int getResolutionInt() {
    return Scalars.intValueExact(resolution);
  }

  public final Scalar getResolution() {
    return resolution;
  }

  public final Tensor getPartitionScale() {
    return partitionScale.unmodifiable();
  }

  public final void printResolution() {
    System.out.println("Resolution = " + resolution);
  }
}
