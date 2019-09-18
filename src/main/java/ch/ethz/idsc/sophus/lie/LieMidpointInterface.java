// code by jph
package ch.ethz.idsc.sophus.lie;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;

public class LieMidpointInterface implements MidpointInterface {
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;

  /** @param lieGroup non-null
   * @param lieExponential non-null */
  public LieMidpointInterface(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieGroup = Objects.requireNonNull(lieGroup);
    this.lieExponential = Objects.requireNonNull(lieExponential);
  }

  @Override // from MidpointInterface
  public Tensor midpoint(Tensor p, Tensor q) {
    LieGroupElement lieGroupElement = lieGroup.element(p);
    LieGroupElement inverse = lieGroupElement.inverse();
    Tensor log = lieExponential.log(inverse.combine(q));
    return lieGroupElement.combine(lieExponential.exp(log.multiply(RationalScalar.HALF)));
  }
}
