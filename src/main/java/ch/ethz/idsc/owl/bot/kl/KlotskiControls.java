// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum KlotskiControls {
  ;
  static List<Flow> of(Tensor board) {
    List<Flow> controls = new ArrayList<>();
    for (int index = 0; index < board.length(); ++index) {
      controls.add(StateSpaceModels.createFlow(KlotskiModel.INSTANCE, Tensors.vector(index, -1, 0)));
      controls.add(StateSpaceModels.createFlow(KlotskiModel.INSTANCE, Tensors.vector(index, +1, 0)));
      controls.add(StateSpaceModels.createFlow(KlotskiModel.INSTANCE, Tensors.vector(index, 0, -1)));
      controls.add(StateSpaceModels.createFlow(KlotskiModel.INSTANCE, Tensors.vector(index, 0, +1)));
    }
    return controls;
  }
}
