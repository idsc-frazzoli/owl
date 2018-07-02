// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.map.Se2GroupAction;
import ch.ethz.idsc.tensor.Tensor;

/** measures difference between p and q in SE2 Lie-Group relative to p
 * difference(p, q) = Inv[p] . q */
public class Se2GroupWrap extends Se2Wrap {
  public Se2GroupWrap(Tensor scale) {
    super(scale);
  }

  @Override // from Se2Wrap
  protected Tensor difference(Tensor p, Tensor q) {
    Tensor p_inv = new Se2GroupAction(p).inverse();
    return new Se2GroupAction(p_inv).circ(q);
  }
}
