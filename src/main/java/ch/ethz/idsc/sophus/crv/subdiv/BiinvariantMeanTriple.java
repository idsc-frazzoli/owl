// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;

/* package */ class BiinvariantMeanTriple implements TripleCenter, Serializable {
  private final BiinvariantMean biinvariantMean;
  private final Tensor cgw;

  public BiinvariantMeanTriple(BiinvariantMean biinvariantMean, Tensor cgw) {
    this.biinvariantMean = Objects.requireNonNull(biinvariantMean);
    this.cgw = cgw;
  }

  @Override // from TripleCenter
  public Tensor midpoint(Tensor p, Tensor q, Tensor r) {
    return biinvariantMean.mean(Unprotect.byRef(p, q, r), cgw);
  }
}
