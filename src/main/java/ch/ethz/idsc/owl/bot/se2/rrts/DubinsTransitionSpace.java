// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparator;
import ch.ethz.idsc.sophus.crv.dubins.FixedRadiusDubins;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class DubinsTransitionSpace implements Se2TransitionSpace, Serializable {
  /** @param radius
   * @param comparator
   * @return */
  public static TransitionSpace of(Scalar radius, Comparator<DubinsPath> comparator) {
    return new DubinsTransitionSpace(radius, comparator);
  }

  /** @param radius
   * @return shortest length dubins path factory */
  public static TransitionSpace of(Scalar radius) {
    return of(radius, DubinsPathComparator.LENGTH);
  }

  // ---
  private final Scalar radius;
  private final Comparator<DubinsPath> comparator;

  private DubinsTransitionSpace(Scalar radius, Comparator<DubinsPath> comparator) {
    this.radius = radius;
    this.comparator = comparator;
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    DubinsPath dubinsPath = FixedRadiusDubins.of(start, end, radius).allValid().min(comparator).get();
    return new DubinsTransition(start, end, dubinsPath);
  }

  /** @param start
   * @param end
   * @return dubins path with minimal length if any */
  public Optional<DubinsPath> dubinsPath(Tensor start, Tensor end) {
    return FixedRadiusDubins.of(start, end, radius).allValid().min(comparator);
  }
}
