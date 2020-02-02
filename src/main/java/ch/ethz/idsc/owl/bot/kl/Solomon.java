// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Solomon {
  SIMPLE( //
      Tensors.vector(5, 1, 2)), //
  /** 19 */
  INSTANCE( //
      Tensors.vector(6, 4, 4), //
      // ---
      Tensors.vector(1, 1, 5), //
      Tensors.vector(1, 2, 2), //
      Tensors.vector(1, 2, 4), //
      // ---
      Tensors.vector(2, 1, 3), //
      Tensors.vector(2, 5, 2), //
      // ---
      Tensors.vector(3, 2, 3), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 3, 5), //
      Tensors.vector(3, 4, 2), //
      Tensors.vector(3, 4, 3), //
      Tensors.vector(3, 4, 4), //
      // ---
      Tensors.vector(4, 3, 1));

  private final Tensor tensor;

  private Solomon(Tensor... tensor) {
    this.tensor = Tensors.of(tensor);
  }

  public KlotskiProblem create() {
    return KlotskiAdapter.create( //
        tensor, //
        name(), //
        Tensors.vector(7, 7), //
        Tensors.vector(6, 1, 1), //
        Tensors.of( //
            Tensors.vector(0, 0), //
            Tensors.vector(7, 0), //
            Tensors.vector(7, 7), //
            Tensors.vector(0, 7), //
            Tensors.vector(0, 3), //
            Tensors.vector(1, 3), //
            Tensors.vector(1, 6), //
            Tensors.vector(6, 6), //
            Tensors.vector(6, 1), //
            Tensors.vector(0, 1)));
  }

  public static void main(String[] args) {
    INSTANCE.create();
  }
}
