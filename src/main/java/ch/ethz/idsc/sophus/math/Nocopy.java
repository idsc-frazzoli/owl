// code by jph
package ch.ethz.idsc.sophus.math;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;

public final class Nocopy {
  private final List<Tensor> list;

  public Nocopy(int initialCapacity) {
    list = new ArrayList<>(initialCapacity);
  }

  public Nocopy append(Tensor tensor) {
    list.add(tensor);
    return this;
  }

  public Tensor tensor() {
    return Unprotect.using(list);
  }
}
