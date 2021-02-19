// code by jph
package ch.ethz.idsc.owl.bot.kd;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Klondike {
  ;
  static final Tensor BOARD = Tensors.of( //
      Tensors.vector(4, 7, 7), //
      Tensors.vector(5, 4, 4, 8, 3, 3, 4, 6, 3), //
      Tensors.vector(1, 4, 5, 1, 1, 1, 4, 5, 1, 7, 1, 3, 5), //
      Tensors.vector(4, 9, 4, 9, 6, 7, 5, 5, 5, 8, 7, 6, 6, 8, 5), //
      Tensors.vector(3, 7, 2, 9, 8, 3, 5, 6, 7, 3, 9, 1, 8, 7, 5, 8, 5), //
      Tensors.vector(1, 4, 7, 8, 4, 2, 9, 2, 7, 1, 1, 8, 2, 2, 7, 6, 3), //
      Tensors.vector(7, 2, 1, 8, 5, 5, 3, 1, 1, 3, 1, 3, 3, 4, 2, 8, 6, 1, 3), //
      Tensors.vector(4, 2, 6, 7, 2, 5, 2, 4, 2, 2, 5, 4, 3, 2, 8, 1, 7, 7, 3), //
      Tensors.vector(4, 1, 6, 5, 1, 1, 1, 9, 1, 4, 3, 4, 4, 3, 1, 9, 8, 2, 7), //
      Tensors.vector(4, 3, 5, 2, 3, 2, 2, 3, 2, 4, 2, 5, 3, 5, 1, 1, 3, 5, 5, 3, 7), //
      Tensors.vector(2, 7, 1, 5, 1, 1, 3, 1, 5, 3, 3, 2, 4, 2, 3, 7, 7, 5, 4, 2, 7), //
      Tensors.vector(2, 5, 2, 2, 6, 1, 2, 4, 4, 6, 3, 4, 1, 2, 1, 2, 6, 5, 1, 8, 8), //
      Tensors.vector(4, 3, 7, 4, 1, 9, 3, 4, 4, 5, 2, 9, 4, 1, 9, 5, 7, 4, 8), //
      Tensors.vector(4, 1, 6, 7, 8, 3, 4, 3, 4, 1, 3, 1, 2, 3, 2, 3, 6, 2, 4), //
      Tensors.vector(7, 3, 2, 6, 1, 5, 3, 9, 2, 3, 2, 1, 5, 7, 5, 8, 9, 5, 4), //
      Tensors.vector(1, 6, 7, 3, 4, 8, 1, 2, 1, 2, 1, 2, 2, 8, 9, 4, 1), //
      Tensors.vector(2, 5, 4, 7, 8, 7, 5, 6, 1, 3, 5, 7, 8, 7, 2, 9, 3), //
      Tensors.vector(6, 5, 6, 4, 6, 7, 2, 5, 2, 2, 6, 3, 4, 7, 4), //
      Tensors.vector(2, 3, 1, 2, 3, 3, 3, 2, 1, 3, 2, 1, 1), //
      Tensors.vector(7, 4, 4, 5, 7, 3, 4, 4, 7), //
      Tensors.vector(3, 3, 4));
}
