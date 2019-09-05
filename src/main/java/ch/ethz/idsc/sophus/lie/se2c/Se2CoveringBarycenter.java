// code by jph
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.sophus.lie.se2.Se2Skew;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** given sequence and mean the implementation computes the weights that satisfy
 * 
 * Se2CoveringBiinvariantMean[sequence, weights] == mean */
public class Se2CoveringBarycenter implements TensorUnaryOperator {
  private static final Tensor RHS = UnitVector.of(4, 3);
  // ---
  private final Tensor sequence;

  /** @param sequence of length 4 */
  public Se2CoveringBarycenter(Tensor sequence) {
    if (sequence.length() != 4)
      throw TensorRuntimeException.of(sequence);
    this.sequence = sequence;
  }

  public Tensor matrix(Tensor mean) {
    return Tensor.of(sequence.stream() //
        .map(new Se2CoveringGroupElement(mean).inverse()::combine) //
        .map(xya -> Se2Skew.of(xya, RealScalar.ONE).rhs() //
            .append(xya.Get(2)) // biinvariant mean of angles
            .append(RealScalar.ONE) // weights are affine
        ));
  }

  @Override
  public Tensor apply(Tensor mean) {
    return LinearSolve.of(Transpose.of(matrix(mean)), RHS);
  }
}
