// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.util.Objects;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class BiinvariantMeanTriple implements TensorUnaryOperator {
  private final BiinvariantMean biinvariantMean;
  private final Tensor cgw;

  /** @param biinvariantMean
   * @param cgw for instance {1/128, 63/64, 1/128} */
  public BiinvariantMeanTriple(BiinvariantMean biinvariantMean, Tensor cgw) {
    this.biinvariantMean = Objects.requireNonNull(biinvariantMean);
    this.cgw = VectorQ.requireLength(cgw, 3);
  }

  @Override // from TripleCenter
  public Tensor apply(Tensor pqr) {
    return biinvariantMean.mean(pqr, cgw);
  }
}
