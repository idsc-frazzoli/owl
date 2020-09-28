// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparators;
import ch.ethz.idsc.sophus.crv.dubins.FixedRadiusDubins;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

public class DubinsTransitionSpace implements TransitionSpace, Serializable {
  /** @param radius positive
   * @param comparator
   * @return
   * @see DubinsPathComparators */
  public static TransitionSpace of(Scalar radius, Comparator<DubinsPath> comparator) {
    return new DubinsTransitionSpace( //
        Sign.requirePositive(radius), //
        Objects.requireNonNull(comparator));
  }

  /***************************************************/
  private final Scalar radius;
  private final Comparator<DubinsPath> comparator;

  private DubinsTransitionSpace(Scalar radius, Comparator<DubinsPath> comparator) {
    this.radius = radius;
    this.comparator = comparator;
  }

  @Override // from TransitionSpace
  public DubinsTransition connect(Tensor start, Tensor end) {
    return new DubinsTransition(start, end, dubinsPath(start, end));
  }

  private DubinsPath dubinsPath(Tensor start, Tensor end) {
    return FixedRadiusDubins.of(start, end, radius).stream().min(comparator).get();
  }
}
