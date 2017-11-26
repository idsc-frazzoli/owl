// code by jph
package ch.ethz.idsc.owl.math.region;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** region is open
 * coordinates on the boundary are inside
 * same convention as {@link ImplicitFunctionRegion} */
public class BoundedBoxRegion implements Region<Tensor> {
  private final Tensor lo;
  private final Tensor hi;

  public BoundedBoxRegion(Tensor center, Tensor radius) {
    lo = center.subtract(radius);
    hi = center.add(radius);
  }

  @Override
  public boolean isMember(Tensor tensor) {
    return IntStream.range(0, lo.length()) //
        .allMatch(index -> Scalars.lessEquals(lo.Get(index), tensor.Get(index)) //
            && Scalars.lessEquals(tensor.Get(index), hi.Get(index)));
  }
}
