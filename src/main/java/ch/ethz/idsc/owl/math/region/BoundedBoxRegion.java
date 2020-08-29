// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** region is open
 * coordinates on the boundary are inside */
public class BoundedBoxRegion implements Region<Tensor>, Serializable {
  /** @param center
   * @param radius for each coordinate
   * @return */
  public static Region<Tensor> fromCenterAndRadius(Tensor center, Tensor radius) {
    Tensor lo = center.subtract(radius);
    Tensor hi = center.add(radius);
    return new BoundedBoxRegion(IntStream.range(0, center.length()) //
        .mapToObj(index -> Clips.interval(lo.Get(index), hi.Get(index))) //
        .collect(Collectors.toList()));
  }

  /***************************************************/
  private final List<Clip> list;

  private BoundedBoxRegion(List<Clip> list) {
    this.list = list;
  }

  @Override
  public boolean isMember(Tensor tensor) {
    return IntStream.range(0, list.size()).allMatch(index -> list.get(index).isInside(tensor.Get(index)));
  }
}
