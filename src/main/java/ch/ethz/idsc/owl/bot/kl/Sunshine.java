// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Sunshine {
  REDUCED( //
      Tensors.vector(8, 10, 5), //
      // Tensors.vector(1, 5, 7), //
      // Tensors.vector(1, 8, 7), //
      // Tensors.vector(1, 10, 2), //
      // Tensors.vector(1, 10, 12), //
      // Tensors.vector(1, 13, 2), //
      // Tensors.vector(1, 13, 12), //
      // ---
      // Tensors.vector(2, 7, 5), //
      // Tensors.vector(2, 7, 8), //
      // ---
      // Tensors.vector(0, 0, 0), //
      // Tensors.vector(0, 0, 3), //
      // Tensors.vector(0, 0, 5), //
      // Tensors.vector(0, 0, 8), //
      // Tensors.vector(0, 0, 10), //
      // Tensors.vector(0, 0, 13), //
      // ---
      // Tensors.vector(0, 3, 0), //
      // Tensors.vector(0, 3, 3), //
      // Tensors.vector(0, 3, 5), //
      // Tensors.vector(0, 3, 8), //
      // Tensors.vector(0, 3, 10), //
      // Tensors.vector(0, 3, 13), //
      // ---
      Tensors.vector(3, 0, 2), //
      Tensors.vector(3, 0, 7), //
      Tensors.vector(3, 0, 12), //
      Tensors.vector(3, 1, 2), //
      Tensors.vector(3, 1, 12), //
      // ---
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 12), //
      Tensors.vector(3, 4, 2), //
      Tensors.vector(3, 4, 7), //
      Tensors.vector(3, 4, 12), //
      // ---
      Tensors.vector(5, 5, 0), //
      Tensors.vector(5, 5, 10)), //
  ORIGINAL( //
      Tensors.vector(8, 10, 5), //
      Tensors.vector(1, 5, 7), //
      Tensors.vector(1, 8, 7), //
      Tensors.vector(1, 10, 2), //
      Tensors.vector(1, 10, 12), //
      Tensors.vector(1, 13, 2), //
      Tensors.vector(1, 13, 12), //
      // ---
      Tensors.vector(2, 7, 5), //
      Tensors.vector(2, 7, 8), //
      // ---
      Tensors.vector(2, 12, 0), //
      Tensors.vector(2, 12, 3), //
      Tensors.vector(2, 12, 10), //
      Tensors.vector(2, 12, 13), //
      // ---
      Tensors.vector(0, 0, 0), //
      Tensors.vector(0, 0, 3), //
      Tensors.vector(0, 0, 5), //
      Tensors.vector(0, 0, 8), //
      Tensors.vector(0, 0, 10), //
      Tensors.vector(0, 0, 13), //
      // ---
      Tensors.vector(0, 3, 0), //
      Tensors.vector(0, 3, 3), //
      Tensors.vector(0, 3, 5), //
      Tensors.vector(0, 3, 8), //
      Tensors.vector(0, 3, 10), //
      Tensors.vector(0, 3, 13), //
      // ---
      Tensors.vector(0, 5, 5), //
      Tensors.vector(0, 5, 8), //
      Tensors.vector(0, 8, 5), //
      Tensors.vector(0, 8, 8), //
      // ---
      Tensors.vector(0, 10, 0), //
      Tensors.vector(0, 10, 3), //
      Tensors.vector(0, 10, 10), //
      Tensors.vector(0, 10, 13), //
      // ---
      Tensors.vector(0, 13, 0), //
      Tensors.vector(0, 13, 3), //
      Tensors.vector(0, 13, 10), //
      Tensors.vector(0, 13, 13), //
      // ---
      Tensors.vector(3, 0, 2), //
      Tensors.vector(3, 0, 7), //
      Tensors.vector(3, 0, 12), //
      Tensors.vector(3, 1, 2), //
      Tensors.vector(3, 1, 12), //
      // ---
      Tensors.vector(3, 2, 0), //
      Tensors.vector(3, 2, 1), //
      Tensors.vector(3, 2, 2), //
      Tensors.vector(3, 2, 3), //
      Tensors.vector(3, 2, 4), //
      Tensors.vector(3, 2, 5), //
      Tensors.vector(3, 2, 7), //
      Tensors.vector(3, 2, 9), //
      Tensors.vector(3, 2, 10), //
      Tensors.vector(3, 2, 11), //
      Tensors.vector(3, 2, 12), //
      Tensors.vector(3, 2, 13), //
      Tensors.vector(3, 2, 14), //
      // ---
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 12), //
      Tensors.vector(3, 4, 2), //
      Tensors.vector(3, 4, 7), //
      Tensors.vector(3, 4, 12), //
      // ---
      Tensors.vector(3, 10, 5), //
      Tensors.vector(3, 10, 9), //
      Tensors.vector(3, 12, 2), //
      Tensors.vector(3, 12, 12), //
      Tensors.vector(3, 14, 5), //
      Tensors.vector(3, 14, 9), //
      // ---
      Tensors.vector(5, 5, 0), //
      Tensors.vector(5, 5, 10));

  private final Tensor tensor;

  private Sunshine(Tensor... tensor) {
    this.tensor = Tensors.of(tensor);
  }

  public KlotskiProblem create() {
    return KlotskiAdapter.create( //
        tensor, //
        "Sunrise." + name(), //
        new MirrorYStateTimeRaster(15), //
        Tensors.vector(15, 15), //
        Tensors.vector(0, 0, 5), //
        Tensors.fromString("{{0, 5}, {0, 0}, {15, 0}, {15, 15}, {0, 15}, {0, 10}}"), //
        Tensors.of( //
            Tensors.vector(0, 0), //
            Tensors.vector(17, 0), //
            Tensors.vector(17, 17), //
            Tensors.vector(0, 17), //
            Tensors.vector(0, 11), //
            Tensors.vector(1, 11), //
            Tensors.vector(1, 16), //
            Tensors.vector(16, 16), //
            Tensors.vector(16, 1), //
            Tensors.vector(1, 1), //
            Tensors.vector(1, 6), //
            Tensors.vector(0, 6)).map(RealScalar.ONE.negate()::add));
  }
}
