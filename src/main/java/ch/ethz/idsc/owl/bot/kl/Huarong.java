// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Huarong {
  /** type (0, 0)
   * steps 19 */
  ONLY_18_STEPS( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(3, 0, 0), //
      Tensors.vector(3, 0, 3), //
      Tensors.vector(3, 1, 0), //
      Tensors.vector(3, 1, 3), //
      Tensors.vector(3, 2, 0), //
      Tensors.vector(3, 2, 1), //
      Tensors.vector(3, 2, 2), //
      Tensors.vector(3, 2, 3), //
      Tensors.vector(3, 3, 0), //
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 4, 0), //
      Tensors.vector(3, 4, 3)),
  /** type (0, 5)
   * steps 44
   * not part of Klotski app */
  ZAURON( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(2, 2, 1), //
      Tensors.vector(2, 3, 0), //
      Tensors.vector(2, 3, 2), //
      Tensors.vector(2, 4, 0), //
      Tensors.vector(2, 4, 2), //
      // ---
      Tensors.vector(3, 0, 0), //
      Tensors.vector(3, 0, 3), //
      Tensors.vector(3, 1, 0), //
      Tensors.vector(3, 1, 3)),
  /** type (3, 0)
   * steps 45 */
  VIOLET( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 0, 0), //
      Tensors.vector(1, 0, 3), //
      Tensors.vector(1, 2, 0), //
      // ---
      Tensors.vector(3, 2, 1), //
      Tensors.vector(3, 2, 2), //
      Tensors.vector(3, 2, 3), //
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 4, 0), //
      Tensors.vector(3, 4, 3)),
  /** type (2, 0)
   * steps 46 */
  DAISY( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 0, 0), //
      Tensors.vector(1, 0, 3), //
      // ---
      Tensors.vector(3, 2, 0), //
      Tensors.vector(3, 2, 1), //
      Tensors.vector(3, 2, 2), //
      Tensors.vector(3, 2, 3), //
      Tensors.vector(3, 3, 0), //
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 4, 0), //
      Tensors.vector(3, 4, 3)),
  /** type (4, 0)
   * steps 46 */
  PANSY( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 0, 0), //
      Tensors.vector(1, 0, 3), //
      Tensors.vector(1, 2, 0), //
      Tensors.vector(1, 2, 3), //
      // ---
      Tensors.vector(3, 2, 1), //
      Tensors.vector(3, 2, 2), //
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 4, 0), //
      Tensors.vector(3, 4, 3)),
  /** type (2, 1)
   * steps 56 */
  POPPY( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 0, 0), //
      Tensors.vector(1, 0, 3), //
      // ---
      Tensors.vector(2, 2, 1), //
      // ---
      Tensors.vector(3, 2, 0), //
      Tensors.vector(3, 2, 3), //
      Tensors.vector(3, 3, 0), //
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 4, 0), //
      Tensors.vector(3, 4, 3)),
  /** type (3, 1)
   * steps 64 */
  SNOWDROP( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 0, 0), //
      Tensors.vector(1, 0, 3), //
      Tensors.vector(1, 2, 0), //
      // ---
      Tensors.vector(2, 2, 1), //
      // ---
      Tensors.vector(3, 2, 3), //
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 4, 0), //
      Tensors.vector(3, 4, 3)),
  /** type (0, 5)
   * steps 77
   * not part of Klotski app */
  BUDDHA( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(2, 2, 0), //
      Tensors.vector(2, 3, 0), //
      Tensors.vector(2, 3, 2), //
      Tensors.vector(2, 4, 0), //
      Tensors.vector(2, 4, 2), //
      // ---
      Tensors.vector(3, 0, 3), //
      Tensors.vector(3, 1, 3), //
      Tensors.vector(3, 2, 2), //
      Tensors.vector(3, 2, 3)),
  /** type (3, 2)
   * steps 91
   * not part of Klotski app */
  CUBISM( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 1, 0), //
      Tensors.vector(1, 3, 2), //
      Tensors.vector(1, 3, 3), //
      // ---
      Tensors.vector(2, 3, 0), //
      Tensors.vector(2, 4, 0), //
      // ---
      Tensors.vector(3, 1, 3), //
      Tensors.vector(3, 2, 1), //
      Tensors.vector(3, 2, 2), //
      Tensors.vector(3, 2, 3)),
  /** type (3, 2)
   * steps 106
   * not part of Klotski app */
  ANDROID( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 0, 0), //
      Tensors.vector(1, 0, 3), //
      Tensors.vector(1, 2, 3), //
      // ---
      Tensors.vector(2, 2, 1), //
      Tensors.vector(2, 3, 1), //
      // ---
      Tensors.vector(3, 2, 0), //
      Tensors.vector(3, 3, 0), //
      Tensors.vector(3, 4, 1), //
      Tensors.vector(3, 4, 2)),
  /** type (4, 1)
   * steps 116 */
  RED_DONKEY( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 0, 0), //
      Tensors.vector(1, 0, 3), //
      Tensors.vector(1, 2, 0), //
      Tensors.vector(1, 2, 3), //
      // ---
      Tensors.vector(2, 2, 1), //
      // ---
      Tensors.vector(3, 3, 1), //
      Tensors.vector(3, 3, 2), //
      Tensors.vector(3, 4, 0), //
      Tensors.vector(3, 4, 3)),
  /** 122
   * CUBISM.19 == HANDSHAKE.50 */
  HANDSHAKE( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 1, 3), //
      Tensors.vector(1, 3, 2), //
      Tensors.vector(1, 3, 3), //
      // ---
      Tensors.vector(2, 3, 0), //
      Tensors.vector(2, 4, 0), //
      // ---
      Tensors.vector(3, 1, 0), //
      Tensors.vector(3, 2, 0), //
      Tensors.vector(3, 2, 1), //
      Tensors.vector(3, 2, 2)),
  /** type (1, 4)
   * steps 124
   * not part of Klotski app */
  BALANCE( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 0, 0), //
      // ---
      Tensors.vector(2, 2, 1), //
      Tensors.vector(2, 3, 0), //
      Tensors.vector(2, 3, 2), //
      Tensors.vector(2, 4, 1), //
      // ---
      Tensors.vector(3, 0, 3), //
      Tensors.vector(3, 1, 3), //
      Tensors.vector(3, 2, 0), //
      Tensors.vector(3, 2, 3)),
  /** type (2, 3)
   * steps 138 */
  TRAIL( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 0, 0), //
      Tensors.vector(1, 0, 3), //
      // ---
      Tensors.vector(2, 2, 1), //
      Tensors.vector(2, 3, 1), //
      Tensors.vector(2, 4, 1), //
      // ---
      Tensors.vector(3, 2, 0), //
      Tensors.vector(3, 2, 3), //
      Tensors.vector(3, 3, 0), //
      Tensors.vector(3, 3, 3)),
  /** type (2, 3)
   * steps 158 */
  AMBUSH( //
      Tensors.vector(0, 0, 1), //
      // ---
      Tensors.vector(1, 1, 0), //
      Tensors.vector(1, 1, 3), //
      // ---
      Tensors.vector(2, 2, 1), //
      Tensors.vector(2, 3, 1), //
      Tensors.vector(2, 4, 1), //
      // ---
      Tensors.vector(3, 0, 0), //
      Tensors.vector(3, 0, 3), //
      Tensors.vector(3, 3, 0), //
      Tensors.vector(3, 3, 3));

  private final Tensor tensor;

  private Huarong(Tensor... tensor) {
    this.tensor = Tensors.of(tensor);
  }

  public KlotskiProblem create() {
    return KlotskiAdapter.create( //
        tensor, //
        "Huarong." + name(), //
        new MirrorYStateTimeRaster(4), //
        Tensors.vector(5, 4), //
        Tensors.vector(0, 3, 1), //
        Tensors.fromString("{{5, 1}, {5, 0}, {0, 0}, {0, 4}, {5, 4}, {5, 3}}"), //
        Tensors.of( //
            Tensors.vector(0, 0), //
            Tensors.vector(7, 0), //
            Tensors.vector(7, 2), //
            Tensors.vector(6, 2), //
            Tensors.vector(6, 1), //
            Tensors.vector(1, 1), //
            Tensors.vector(1, 5), //
            Tensors.vector(6, 5), //
            Tensors.vector(6, 4), //
            Tensors.vector(7, 4), //
            Tensors.vector(7, 6), //
            Tensors.vector(0, 6)).map(RealScalar.ONE.negate()::add));
  }
}
