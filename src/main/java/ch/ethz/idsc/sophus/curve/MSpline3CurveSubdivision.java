// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** cubic B-spline
 * 
 * biinvariant mean */
public final class MSpline3CurveSubdivision extends RefiningBSpline3CurveSubdivision {
  private static final Tensor MASKC = Tensors.vector(1, 6, 1).divide(RealScalar.of(8));
  private static final Tensor MASKD = Tensors.vector(4, 4).divide(RealScalar.of(8));
  // ---
  private final BiinvariantMean biinvariantMean;

  public MSpline3CurveSubdivision(BiinvariantMean biinvariantMean) {
    this.biinvariantMean = biinvariantMean;
  }

  @Override
  protected Tensor center(Tensor q, Tensor r) {
    return biinvariantMean.mean(Tensors.of(q, r), MASKD);
  }

  @Override
  protected Tensor center(Tensor p, Tensor q, Tensor r) {
    return biinvariantMean.mean(Tensors.of(p, q, r), MASKC);
  }
}
