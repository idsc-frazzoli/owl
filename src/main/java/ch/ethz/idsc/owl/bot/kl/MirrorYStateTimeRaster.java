// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.Serializable;

import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.order.VectorLexicographic;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;

/* package */ class MirrorYStateTimeRaster implements StateTimeRaster, Serializable {
  // ---
  private final int sy;

  public MirrorYStateTimeRaster(int sy) {
    this.sy = sy;
  }

  public Tensor mirror(Tensor state) {
    return Tensor.of(state.stream().map(this::mirrorStone));
  }

  public Tensor mirrorStone(Tensor stone) {
    int type = Scalars.intValueExact(stone.Get(0));
    int px = Scalars.intValueExact(stone.Get(1));
    int py = Scalars.intValueExact(stone.Get(2));
    return Tensors.vector(type, px, sy - py - Block.values()[type].wy);
  }

  @Override // from StateTimeRaster
  public Tensor convertToKey(StateTime stateTime) {
    Tensor state = stateTime.state();
    Tensor key1 = KlotskiStateTimeRaster.convertToKey(state);
    Tensor key2 = KlotskiStateTimeRaster.convertToKey(mirror(state));
    return VectorLexicographic.COMPARATOR.compare( //
        Flatten.of(key1), //
        Flatten.of(key2)) < 1 //
            ? key1
            : key2;
  }
}
