// code by jph
package ch.ethz.idsc.sophus.group;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

/** element of n-dimensional Scaling and Translations group
 * 
 * <p>the neutral element is {{1, ..., 1}, {0, ..., 0}}
 * 
 * <p>Reference:
 * Bi-invariant Means in Lie Groups.
 * Application to Left-invariant Polyaffine Transformations.
 * by Vincent Arsigny, Xavier Pennec, Nicholas Ayache
 * pages 27-31 */
public class StGroupElement implements LieGroupElement, Serializable {
  private final Tensor x;
  private final Tensor y;

  /** @param xy of the form {{x1, ...., xn}, {y1, ..., yn}} */
  // Fehlt requirement für x grösser 0?
  public StGroupElement(Tensor xya) {
    this( //
        Sign.requirePositive(xya.Get(0)), // Hier stimmt etwas mit dem dimensionen vllt nicht. nur Skalare möglich?
        xya.Get(1)); //
  }

  private StGroupElement(Tensor x, Tensor y) {
    this.x = x;
    this.y = y;
  }

  @Override // from LieGroupElement
  public StGroupElement inverse() {
    return new StGroupElement( //
        x.Get().reciprocal(), //
        y.negate().divide(x.Get()));
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor xya) {
    StGroupElement StGroupElement = new StGroupElement(xya);
    return Tensors.of( //
        x.dot(StGroupElement.x), //
        x.dot(StGroupElement.y).add(y));
  }

  // function for convenience and testing
  public Tensor toTensor() {
    return Tensors.of(x, y);
  }
}
