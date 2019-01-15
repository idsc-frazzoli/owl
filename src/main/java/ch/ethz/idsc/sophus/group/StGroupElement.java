// code by jph
package ch.ethz.idsc.sophus.group;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;

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
  public StGroupElement(Tensor xy) {
    this( //
        VectorQ.require(xy.get(0)), //
        VectorQ.require(xy.get(1))); //
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
  public Tensor combine(Tensor xy) {
    StGroupElement StGroupElement = new StGroupElement(xy);
    return Tensors.of( //
        x.dot(StGroupElement.x), //
        x.dot(StGroupElement.y).add(y));
  }

  // function for convenience and testing
  public Tensor toTensor() {
    return Tensors.of(x, y);
  }
}
