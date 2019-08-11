// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.BinaryAverage;

@FunctionalInterface
public interface SplitInterface extends MidpointInterface, BinaryAverage {
  @Override // from MidpointInterface
  default Tensor midpoint(Tensor p, Tensor q) {
    return split(p, q, RationalScalar.HALF);
  }
}
