// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/** represents the standard state-space model with
 * state matrix a,
 * input matrix b,
 * output matrix c, and
 * transmission matrix d */
class LinearStateSpaceModel implements StateSpaceModel {
  private final Tensor a;
  private final Tensor b;
  private final Tensor c;
  private final Tensor d;
  private final Scalar L;

  public LinearStateSpaceModel(Tensor a, Tensor b, Tensor c, Tensor d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    L = Norm._2.ofMatrix(a);
  }

  @Override
  public Tensor f(Tensor x, Tensor u) {
    return a.dot(x).add(b.dot(u));
  }

  public Tensor output(Tensor x, Tensor u) {
    return c.dot(x).add(d.dot(u));
  }

  @Override
  public Scalar getLipschitz() {
    return L;
  }
}
