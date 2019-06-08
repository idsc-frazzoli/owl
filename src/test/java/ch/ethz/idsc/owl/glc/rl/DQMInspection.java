// code by jph
package ch.ethz.idsc.owl.glc.rl;

import java.util.Map;
import java.util.Map.Entry;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.sca.Increment;

/* package */ class DQMInspection {
  private final Tensor min;
  private final Tensor max;
  private final Tensor count;

  public DQMInspection(Map<Tensor, RLDomainQueue> rlDomainQueueMap) {
    min = rlDomainQueueMap.keySet().stream().reduce(Entrywise.min()).get();
    max = rlDomainQueueMap.keySet().stream().reduce(Entrywise.max()).get();
    Tensor width = max.subtract(min).map(Increment.ONE);
    if (!ExactTensorQ.of(width))
      throw TensorRuntimeException.of(min, max, width);
    count = Array.zeros(Primitives.toListInteger(width));
    for (Entry<Tensor, RLDomainQueue> entry : rlDomainQueueMap.entrySet()) {
      Tensor key = entry.getKey();
      RLDomainQueue value = entry.getValue();
      Integer[] array = Primitives.toListInteger(key.subtract(min)).toArray(new Integer[min.length()]);
      count.set(RealScalar.of(value.size()), array);
    }
  }

  public Tensor getCount() {
    return count;
  }
}
