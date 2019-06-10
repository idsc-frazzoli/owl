// code by ob, jph
package ch.ethz.idsc.sophus.group;

import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** @param sequence of (x, y, a) points in SE(2) and weights non-negative and normalized
 * rotation angles a_i have to satisfy: sup (i,j) |ai-aj| <= pi - C
 * @return associated biinvariant mean which is the solution to the barycentric equation
 * 
 * For the rigid motion in 2D an explicit solution for the biinvariant mean exists.
 * 
 * Reference:
 * "Bi-invariant Means in Lie Groups. Application to left-invariant Polyaffine Transformations." p.38
 * Vincent Arsigny, Xavier Pennec, Nicholas Ayache */
public enum Se2BiinvariantMean implements BiinvariantMean {
  /** Arsigny-formula yields better results in BiinvariantMeanCenter
   * however, the operation domain is reduced compared to the default-formula */
  LINEAR(So2LinearBiinvariantMean.INSTANCE), //
  /** default-formula is defined globally for arbitrary angles and weights */
  GLOBAL(So2GlobalBiinvariantMean.INSTANCE), //
  ;
  // ---
  private static final Scalar ZERO = RealScalar.ZERO;
  // ---
  private final ScalarBiinvariantMean scalarBiinvariantMean;

  private Se2BiinvariantMean(ScalarBiinvariantMean scalarBiinvariantMean) {
    this.scalarBiinvariantMean = scalarBiinvariantMean;
  }

  @Override // from BiinvariantMeanInterface
  public Tensor mean(Tensor sequence, Tensor weights) {
    AffineQ.require(weights);
    Scalar amean = scalarBiinvariantMean.mean(sequence.get(Tensor.ALL, 2), weights);
    // make transformation s.t. mean rotation is zero and retransformation after taking mean
    Se2GroupElement transfer = new Se2GroupElement(Tensors.of(ZERO, ZERO, amean));
    // Tensor transferred = Tensor.of(sequence.stream().map(transfer.inverse()::combine));
    // Tensor tmean = LinearSolve.of( //
    // weights.dot(transferred.get(Tensor.ALL, 2).negate().map(So2Skew::of)), //
    // weights.dot(Tensor.of(transferred.stream().map(Se2Skew.FUNCTION))));
    // return transfer.combine(tmean.append(ZERO));
    AtomicInteger index = new AtomicInteger(-1);
    Tensor tmean = sequence.stream() //
        .map(transfer.inverse()::combine) //
        .map(xya -> Se2Decomp.of(xya, weights.Get(index.incrementAndGet()))) //
        .reduce(Se2Decomp::add) //
        .get().solve();
    return transfer.combine(tmean.append(ZERO));
  }
}
