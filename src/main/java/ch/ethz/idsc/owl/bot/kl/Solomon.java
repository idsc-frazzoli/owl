// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Solomon {
  SIMPLE( //
      Tensors.vector(7, 0, 1)), //
  /** 19 */
  INSTANCE( //
      Tensors.vector(7, 3, 3), //
      // ---
      Tensors.vector(1, 0, 4), //
      Tensors.vector(1, 1, 1), //
      Tensors.vector(1, 1, 3), //
      // ---
      Tensors.vector(2, 0, 2), //
      Tensors.vector(2, 4, 1), //
      // ---
      Tensors.vector(3, 1, 2), //
      Tensors.vector(3, 2, 2), //
      Tensors.vector(3, 2, 4), //
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 3), //
      // ---
      Tensors.vector(4, 2, 0));

  private final Tensor tensor;

  private Solomon(Tensor... tensor) {
    this.tensor = Tensors.of(tensor);
  }

  public KlotskiProblem create() {
    return KlotskiAdapter.create( //
        tensor, //
        "Solomon." + name(), //
        KlotskiStateTimeRaster.INSTANCE, //
        Tensors.vector(5, 5), //
        Tensors.vector(7, 0, 0), //
        Tensors.fromString(""), //
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
            Tensors.vector(0, 1)).map(RealScalar.ONE.negate()::add));
  }

  public static void main(String[] args) {
    INSTANCE.create();
  }
}
