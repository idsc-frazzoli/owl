// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum KlotskiControls {
  ;
  static List<Tensor> of(Tensor board) {
    List<Tensor> controls = new ArrayList<>();
    for (int index = 0; index < board.length(); ++index) {
      controls.add(Tensors.vector(index, -1, 0));
      controls.add(Tensors.vector(index, +1, 0));
      controls.add(Tensors.vector(index, 0, -1));
      controls.add(Tensors.vector(index, 0, +1));
    }
    return controls;
  }
}
