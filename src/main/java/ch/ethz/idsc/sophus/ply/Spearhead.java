// code by jph
package ch.ethz.idsc.sophus.ply;

import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransition;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;

public enum Spearhead {
  ;
  /** @param p
   * @param width
   * @return */
  public static Tensor of(Tensor p, Scalar width) {
    Tensor tip = Tensors.vector(1, 0, Math.PI);
    Tensor q = p.pmul(Tensors.vector(1, -1, -1));
    Tensor cp = Tensors.of(p, tip, q);
    Tensor curve = Tensors.empty();
    for (int count = 0; count < 3; ++count) {
      Tensor p1 = cp.get(count);
      Tensor p2 = cp.get((count + 1) % 3);
      curve.append(ClothoidTransition.of(p1, flip(p2)).linearized(width));
    }
    return Tensor.of(curve.flatten(1).map(Extract2D.FUNCTION));
  }

  private static Tensor flip(Tensor p) {
    Tensor q = p.copy();
    q.set(Pi.VALUE::add, 2);
    q.set(So2.MOD, 2);
    return q;
  }
}
