// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Pennant implements KlotskiProblem {
  PUZZLE( //
      Tensors.vector(0, 1, 1), //
      // ---
      Tensors.vector(1, 4, 1), //
      Tensors.vector(1, 4, 2), //
      // ---
      Tensors.vector(2, 1, 3), //
      Tensors.vector(2, 2, 3), //
      Tensors.vector(2, 4, 3), //
      Tensors.vector(2, 5, 3), //
      // ---
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 2));

  private final Tensor tensor;

  private Pennant(Tensor... tensor) {
    this.tensor = Tensors.of(tensor);
  }

  @Override
  public Tensor getBoard() {
    return tensor.copy();
  }

  @Override
  public Tensor getGoal() {
    return Tensors.vector(0, 4, 1);
  }
}
