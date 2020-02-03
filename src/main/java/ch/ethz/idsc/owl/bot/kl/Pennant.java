// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Pennant {
  /** 83 */
  PUZZLE( //
      Tensors.vector(0, 0, 0), //
      // ---
      Tensors.vector(1, 3, 0), //
      Tensors.vector(1, 3, 1), //
      // ---
      Tensors.vector(2, 0, 2), //
      Tensors.vector(2, 1, 2), //
      Tensors.vector(2, 3, 2), //
      Tensors.vector(2, 4, 2), //
      // ---
      Tensors.vector(3, 2, 0), //
      Tensors.vector(3, 2, 1));

  private final Tensor tensor;

  private Pennant(Tensor... tensor) {
    this.tensor = Tensors.of(tensor);
  }

  public KlotskiProblem create() {
    return KlotskiAdapter.create( //
        tensor, //
        "Pennant." + name(), //
        KlotskiStateTimeRaster.INSTANCE, //
        Tensors.vector(5, 4), //
        Tensors.vector(0, 3, 0), //
        Tensors.of( //
            Tensors.vector(0, 0), //
            Tensors.vector(7, 0), //
            Tensors.vector(7, 1), //
            Tensors.vector(1, 1), //
            Tensors.vector(1, 5), //
            Tensors.vector(6, 5), //
            Tensors.vector(6, 3), //
            Tensors.vector(7, 3), //
            Tensors.vector(7, 6), //
            Tensors.vector(0, 6)).map(RealScalar.ONE.negate()::add));
  }
}
