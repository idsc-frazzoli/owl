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

  @Override // from KlotskiProblem
  public Tensor getBoard() {
    return tensor.copy();
  }

  @Override // from KlotskiProblem
  public Tensor size() {
    return Tensors.vector(7, 6);
  }

  @Override // from KlotskiProblem
  public Tensor getGoal() {
    return Tensors.vector(0, 4, 1);
  }

  @Override // from KlotskiProblem
  public Tensor getFrame() {
    return Tensors.of( //
        Tensors.vector(0, 0), //
        Tensors.vector(7, 0), //
        Tensors.vector(7, 1), //
        Tensors.vector(1, 1), //
        Tensors.vector(1, 5), //
        Tensors.vector(6, 5), //
        Tensors.vector(6, 3), //
        Tensors.vector(7, 3), //
        Tensors.vector(7, 6), //
        Tensors.vector(0, 6));
  }
}
