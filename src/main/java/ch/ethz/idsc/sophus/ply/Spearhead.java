// code by jph
package ch.ethz.idsc.sophus.ply;

import java.util.stream.IntStream;

import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransition;
import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.num.Pi;

public enum Spearhead {
  ;
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
  private static final Tensor TIP = Tensors.vector(1, 0, Math.PI);
  private static final Tensor REF = Tensors.vector(1, -1, -1);

  /** @param p
   * @param width
   * @return */
  public static Tensor of(Tensor p, Scalar width) {
    Tensor[] cp = { p, TIP, p.pmul(REF) };
    return Tensor.of(IntStream.range(0, 3) //
        .mapToObj(index -> ClothoidTransition.of(CLOTHOID_BUILDER, cp[index], flip(cp[(index + 1) % 3])).linearized(width)) //
        .flatMap(Tensor::stream) //
        .map(Extract2D.FUNCTION));
  }

  private static Tensor flip(Tensor p) {
    Tensor q = p.copy();
    q.set(Pi.VALUE::add, 2);
    q.set(So2.MOD, 2);
    return q;
  }
}
