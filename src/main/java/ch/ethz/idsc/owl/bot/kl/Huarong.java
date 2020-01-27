// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Huarong implements KlotskiProblem {
  SIMPLE( //
      Tensors.vector(0, 1, 2)), //
  /** 19 */
  ONLY_18_STEPS( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(3, 1, 1), //
      Tensors.vector(3, 1, 4), //
      // ---
      Tensors.vector(3, 2, 1), //
      Tensors.vector(3, 2, 4), //
      // ---
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 3, 4), //
      // ---
      Tensors.vector(3, 4, 1), //
      Tensors.vector(3, 4, 2), //
      Tensors.vector(3, 4, 3), //
      Tensors.vector(3, 4, 4)), //
  /** 46 */
  DAISY( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(1, 1, 1), //
      Tensors.vector(1, 1, 4), //
      // ---
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 3, 4), //
      // ---
      Tensors.vector(3, 4, 1), //
      Tensors.vector(3, 4, 2), //
      Tensors.vector(3, 4, 3), //
      Tensors.vector(3, 4, 4), //
      // ---
      Tensors.vector(3, 5, 1), //
      Tensors.vector(3, 5, 4)), //
  /** 45 */
  VIOLET( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(1, 1, 1), //
      Tensors.vector(1, 1, 4), //
      // ---
      Tensors.vector(1, 3, 1), //
      // ---
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 3, 4), //
      // ---
      Tensors.vector(3, 4, 2), //
      Tensors.vector(3, 4, 3), //
      Tensors.vector(3, 4, 4), //
      // ---
      Tensors.vector(3, 5, 1), //
      Tensors.vector(3, 5, 4)), //
  /** 56 */
  POPPY( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(1, 1, 1), //
      Tensors.vector(1, 1, 4), //
      // ---
      Tensors.vector(2, 3, 2), //
      // ---
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 4), //
      // ---
      Tensors.vector(3, 4, 1), //
      Tensors.vector(3, 4, 2), //
      Tensors.vector(3, 4, 3), //
      Tensors.vector(3, 4, 4), //
      // ---
      Tensors.vector(3, 5, 1), //
      Tensors.vector(3, 5, 4)), //
  /** 46 */
  PANSY( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(1, 1, 1), //
      Tensors.vector(1, 1, 4), //
      // ---
      Tensors.vector(1, 3, 1), //
      Tensors.vector(1, 3, 4), //
      // ---
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 3), //
      // ---
      Tensors.vector(3, 4, 2), //
      Tensors.vector(3, 4, 3), //
      // ---
      Tensors.vector(3, 5, 1), //
      Tensors.vector(3, 5, 4)), //
  /** 64 */
  SNOWDROP( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(1, 1, 1), //
      Tensors.vector(1, 1, 4), //
      Tensors.vector(1, 3, 1), //
      // ---
      Tensors.vector(2, 3, 2), //
      // ---
      Tensors.vector(3, 3, 4), //
      // ---
      Tensors.vector(3, 4, 2), //
      Tensors.vector(3, 4, 3), //
      Tensors.vector(3, 4, 4), //
      // ---
      Tensors.vector(3, 5, 1), //
      Tensors.vector(3, 5, 4)), //
  /** 116 */
  RED_DONKEY( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(1, 1, 1), //
      Tensors.vector(1, 1, 4), //
      Tensors.vector(1, 3, 1), //
      Tensors.vector(1, 3, 4), //
      // ---
      Tensors.vector(2, 3, 2), //
      // ---
      Tensors.vector(3, 4, 2), //
      Tensors.vector(3, 4, 3), //
      // ---
      Tensors.vector(3, 5, 1), //
      Tensors.vector(3, 5, 4)), //
  /** 138 */
  TRAIL( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(1, 1, 1), //
      Tensors.vector(1, 1, 4), //
      // ---
      Tensors.vector(2, 3, 2), //
      Tensors.vector(2, 4, 2), //
      Tensors.vector(2, 5, 2), //
      // ---
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 4), //
      Tensors.vector(3, 4, 1), //
      Tensors.vector(3, 4, 4)), //
  /** 158 */
  AMBUSH( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(1, 2, 1), //
      Tensors.vector(1, 2, 4), //
      // ---
      Tensors.vector(2, 3, 2), //
      Tensors.vector(2, 4, 2), //
      Tensors.vector(2, 5, 2), //
      // ---
      Tensors.vector(3, 1, 1), //
      Tensors.vector(3, 1, 4), //
      Tensors.vector(3, 4, 1), //
      Tensors.vector(3, 4, 4)), //
  // ---
  /** 104 */
  ANDROID( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(1, 1, 1), //
      Tensors.vector(1, 1, 4), //
      Tensors.vector(1, 3, 4), //
      // ---
      Tensors.vector(2, 3, 2), //
      Tensors.vector(2, 4, 2), //
      // Tensors.vector(2, 5, 2), //
      // ---
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 4, 1), //
      Tensors.vector(3, 5, 1), //
      Tensors.vector(3, 5, 4)), //
  /** 124 */
  ONE_FOUR( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(1, 1, 1), //
      // ---
      Tensors.vector(2, 3, 2), //
      Tensors.vector(2, 4, 1), //
      Tensors.vector(2, 4, 3), //
      Tensors.vector(2, 5, 2), //
      // ---
      Tensors.vector(3, 1, 4), //
      Tensors.vector(3, 2, 4), //
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 4)), //
  /** 133 */
  TRYOUT( //
      Tensors.vector(0, 1, 2), //
      // ---
      Tensors.vector(1, 1, 1), //
      Tensors.vector(1, 2, 4), //
      // ---
      Tensors.vector(2, 3, 2), //
      Tensors.vector(2, 4, 2), //
      Tensors.vector(2, 5, 2), //
      // ---
      Tensors.vector(3, 1, 4), //
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 4, 1), //
      Tensors.vector(3, 4, 4));

  private final Tensor tensor;

  private Huarong(Tensor... tensor) {
    this.tensor = Tensors.of(tensor);
  }

  @Override
  public Tensor getBoard() {
    return tensor.copy();
  }

  @Override
  public Tensor size() {
    return Tensors.vector(7, 6);
  }

  @Override
  public Tensor getGoal() {
    return Tensors.vector(0, 4, 2);
  }
}
