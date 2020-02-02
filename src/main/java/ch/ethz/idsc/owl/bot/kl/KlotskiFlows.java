// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.idsc.owl.glc.core.StateTimeFlows;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class KlotskiFlows implements StateTimeFlows, Serializable {
  private final int lx;
  private final int ly;

  public KlotskiFlows(KlotskiProblem klotskiProblem) {
    Tensor size = klotskiProblem.size();
    lx = size.Get(0).number().intValue();
    ly = size.Get(1).number().intValue();
  }

  @Override // from StateTimeFlows
  public Collection<Tensor> flows(StateTime stateTime) {
    Collection<Tensor> controls = new ArrayList<>();
    int index = 0;
    for (Tensor stone : stateTime.state()) {
      int type = stone.Get(0).number().intValue();
      int px = stone.Get(1).number().intValue();
      int py = stone.Get(2).number().intValue();
      if (0 < px)
        controls.add(Tensors.vector(index, -1, 0));
      if (0 < py)
        controls.add(Tensors.vector(index, 0, -1));
      if (px < lx - Block.values()[type].wx)
        controls.add(Tensors.vector(index, +1, 0));
      if (py < ly - Block.values()[type].wy)
        controls.add(Tensors.vector(index, 0, +1));
      ++index;
    }
    return controls;
  }
}
