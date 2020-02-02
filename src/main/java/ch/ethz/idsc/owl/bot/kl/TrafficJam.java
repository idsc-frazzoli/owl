// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum TrafficJam {
  CORNERS_ONLY( //
      Tensors.vector(0, 1, 1), //
      // ---
      Tensors.vector(5, 1, 4), //
      Tensors.vector(5, 4, 1), //
      // ---
      Tensors.vector(6, 2, 5), //
      Tensors.vector(6, 4, 2)), //
  NO_CORNERS( //
      Tensors.vector(0, 1, 1), //
      // ---
      Tensors.vector(1, 4, 5), //
      Tensors.vector(1, 4, 6), //
      // ---
      Tensors.vector(2, 3, 1), //
      // ---
      Tensors.vector(3, 1, 6), //
      Tensors.vector(3, 2, 5), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 3, 4), //
      Tensors.vector(3, 4, 4), //
      Tensors.vector(3, 5, 4)), //
  PROPAEDEUTIC5( //
      Tensors.vector(0, 1, 1), //
      // ---
      Tensors.vector(1, 4, 5), //
      Tensors.vector(1, 4, 6), //
      // ---
      Tensors.vector(2, 3, 1), //
      // ---
      Tensors.vector(3, 1, 6), //
      Tensors.vector(3, 2, 5), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 3, 4), //
      Tensors.vector(3, 4, 4), //
      Tensors.vector(3, 5, 4), //
      // ---
      Tensors.vector(6, 2, 5), //
      Tensors.vector(6, 4, 2)), //
  INSTANCE( //
      Tensors.vector(0, 1, 1), //
      // ---
      Tensors.vector(1, 4, 5), //
      Tensors.vector(1, 4, 6), //
      // ---
      Tensors.vector(2, 3, 1), //
      // ---
      Tensors.vector(3, 1, 6), //
      Tensors.vector(3, 2, 5), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 3, 4), //
      Tensors.vector(3, 4, 4), //
      Tensors.vector(3, 5, 4), //
      // ---
      Tensors.vector(5, 1, 4), //
      Tensors.vector(5, 4, 1), //
      // ---
      Tensors.vector(6, 2, 5), //
      Tensors.vector(6, 4, 2)), //
  ;

  private final Tensor tensor;

  private TrafficJam(Tensor... tensor) {
    this.tensor = Tensors.of(tensor);
  }

  public KlotskiProblem create() {
    return KlotskiAdapter.create( //
        tensor, //
        name(), //
        Tensors.vector(7, 8), //
        Tensors.vector(0, 4, 5), //
        Tensors.of( //
            Tensors.vector(0, 0), //
            Tensors.vector(7, 0), //
            Tensors.vector(7, 8), //
            Tensors.vector(6, 8), //
            Tensors.vector(6, 1), //
            Tensors.vector(1, 1), //
            Tensors.vector(1, 7), //
            Tensors.vector(4, 7), //
            Tensors.vector(4, 8), //
            Tensors.vector(0, 8)));
  }
}
