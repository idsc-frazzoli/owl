// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;

/* package */ class DeltaFlows implements FlowsInterface, Serializable {
  // ---
  private final Scalar amp;

  public DeltaFlows(Scalar amp) {
    this.amp = amp;
  }

  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    Collection<Tensor> collection = new ArrayList<>();
    for (Tensor u : CirclePoints.of(resolution))
      collection.add(u.multiply(amp));
    return collection;
  }
}
