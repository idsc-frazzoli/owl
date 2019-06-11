// code by jph
package ch.ethz.idsc.sophus.lie.he;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** element of (2*n+1)-dimensional Heisenberg group
 * 
 * <p>the neutral element is {{0, ..., 0}, {0, ..., 0}, 0}
 * 
 * <p>Reference:
 * Bi-invariant Means in Lie Groups.
 * Application to Left-invariant Polyaffine Transformations.
 * by Vincent Arsigny, Xavier Pennec, Nicholas Ayache
 * pages 31-32 */
public class HeGroupElement implements LieGroupElement, Serializable {
  private final Tensor x;
  private final Tensor y;
  private final Scalar z;

  /** @param xyz of the form {{x1, ...., xn}, {y1, ..., yn}, z} */
  public HeGroupElement(Tensor xyz) {
    this( //
        VectorQ.require(xyz.get(0)), //
        VectorQ.require(xyz.get(1)), //
        xyz.Get(2));
  }

  private HeGroupElement(Tensor x, Tensor y, Scalar z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override // from LieGroupElement
  public HeGroupElement inverse() {
    return new HeGroupElement( //
        x.negate(), //
        y.negate(), //
        x.dot(y).Get().subtract(z));
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor xyz) {
    HeGroupElement heGroupElement = new HeGroupElement(xyz);
    return Tensors.of( //
        x.add(heGroupElement.x), //
        y.add(heGroupElement.y), //
        z.add(heGroupElement.z).add(x.dot(heGroupElement.y)));
  }

  @Override // from LieGroupElement
  public Tensor adjoint(Tensor dxdydz) {
    return Tensors.of( //
        dxdydz.get(0), //
        dxdydz.get(1), //
        y.negate().dot(dxdydz.get(0)).add(x.dot(dxdydz.get(1))).add(dxdydz.get(2)));
  }

  // function for convenience and testing
  public Tensor toTensor() {
    return Tensors.of(x, y, z);
  }
}
