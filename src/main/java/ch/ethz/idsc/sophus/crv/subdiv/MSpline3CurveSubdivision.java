// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.util.stream.Stream;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
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
  public Tensor midpoint(Tensor q, Tensor r) {
    return biinvariantMean.mean(Tensor.of(Stream.of(q, r)), MASKD);
  }

  @Override
  protected Tensor center(Tensor p, Tensor q, Tensor r) {
    return biinvariantMean.mean(Tensor.of(Stream.of(p, q, r)), MASKC);
  }
}
