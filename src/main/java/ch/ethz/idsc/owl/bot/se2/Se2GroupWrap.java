// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.tensor.Tensor;

/** measures difference between p and q in SE2 Lie-Group relative to p
 * difference(p, q) = Inv[p] . q */
public class Se2GroupWrap extends Se2Wrap {
  public Se2GroupWrap(Tensor scale) {
    super(scale);
  }

  @Override // from Se2Wrap
  protected Tensor difference(Tensor p, Tensor q) {
    Tensor p_inv = new Se2CoveringGroupAction(p).inverse();
    return new Se2CoveringGroupAction(p_inv).combine(q);
  }
}
