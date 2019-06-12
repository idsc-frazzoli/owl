// code by ob, jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.ScalarBiinvariantMean;
import ch.ethz.idsc.sophus.lie.so2.So2FilterBiinvariantMean;
import ch.ethz.idsc.sophus.lie.so2.So2GlobalBiinvariantMean;
import ch.ethz.idsc.sophus.lie.so2.So2LinearBiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** Biinvariant mean for a sequence of points in SE(2), which is the solution to
 * the barycentric equation.
 * 
 * For the rigid motion in 2D an explicit solution for the biinvariant mean exists.
 * 
 * Reference:
 * "Bi-invariant Means in Lie Groups. Application to left-invariant Polyaffine Transformations." p.38
 * Vincent Arsigny, Xavier Pennec, Nicholas Ayache */
public enum Se2BiinvariantMean implements BiinvariantMean {
  /** The Arsigny-formula which treats SO(2) locally as a vector space yields
   * better results in BiinvariantMeanCenter compared to the global formula.
   * However, the operation domain is reduced
   * rotation angles a_i have to satisfy: sup (i,j) |ai-aj| < pi */
  LINEAR(So2LinearBiinvariantMean.INSTANCE), //
  /** Careful:
   * FILTER is NOT invariant under permutation of input parameters!
   * 
   * FILTER is the generalization of LINEAR to arbitrary angles
   * FILTER is suitable for use in center filters */
  FILTER(So2FilterBiinvariantMean.INSTANCE), //
  /** global formula is defined globally for arbitrary angles and weights */
  GLOBAL(So2GlobalBiinvariantMean.INSTANCE), //
  ;
  // ---
  private static final Scalar ZERO = RealScalar.ZERO;
  // ---
  private final ScalarBiinvariantMean scalarBiinvariantMean;

  /** @param scalarBiinvariantMean */
  private Se2BiinvariantMean(ScalarBiinvariantMean scalarBiinvariantMean) {
    this.scalarBiinvariantMean = scalarBiinvariantMean;
  }

  @Override // from BiinvariantMean
  public Tensor mean(Tensor sequence, Tensor weights) {
    Scalar amean = scalarBiinvariantMean.mean(sequence.get(Tensor.ALL, 2), weights);
    return Se2Skew.mean(new Se2GroupElement(Tensors.of(ZERO, ZERO, amean)), sequence, weights);
  }
}
