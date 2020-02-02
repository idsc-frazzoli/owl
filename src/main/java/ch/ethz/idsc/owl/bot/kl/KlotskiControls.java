// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.idsc.owl.glc.core.StateTimeFlows;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class KlotskiControls implements StateTimeFlows, Serializable {
  private final Collection<Tensor> controls = new ArrayList<>();

  public KlotskiControls(Tensor board) {
    for (int index = 0; index < board.length(); ++index) {
      controls.add(Tensors.vector(index, -1, 0));
      controls.add(Tensors.vector(index, +1, 0));
      controls.add(Tensors.vector(index, 0, -1));
      controls.add(Tensors.vector(index, 0, +1));
    }
  }

  @Override
  public Collection<Tensor> flows(StateTime stateTime) {
    return controls;
  }
}
