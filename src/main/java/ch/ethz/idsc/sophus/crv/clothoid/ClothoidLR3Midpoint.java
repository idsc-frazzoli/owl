// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import java.util.stream.Stream;

import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum ClothoidLR3Midpoint implements MidpointInterface {
  INSTANCE;
  // ---
  private static final TensorUnaryOperator TENSOR_UNARY_OPERATOR = //
      new LaneRiesenfeldCurveSubdivision(Clothoid1.INSTANCE, 3)::string;

  @Override // from MidpointInterface
  public Tensor midpoint(Tensor p, Tensor q) {
    // TODO JPH TENSOR 075 Unprotect.byRef, also other occurrence
    return TENSOR_UNARY_OPERATOR.apply(Tensor.of(Stream.of(p, q))).get(1);
  }
}
