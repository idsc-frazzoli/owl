// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Comparator;
import java.util.Objects;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum ArgMinComparator implements Comparator<Tensor> {
  INSTANCE;
  @Override
  public int compare(Tensor t1, Tensor t2) {
    if (Objects.isNull(t1))
      return 1;
    if (Objects.isNull(t2))
      return -1;
    return Scalars.compare(t1.Get(0), t2.Get(0));
  }
}
