// code by jph
package ch.ethz.idsc.owl.math.model;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

/** represents the standard state-space model with
 * state matrix a,
 * input matrix b,
 * output matrix c, and
 * transmission matrix d
 * 
 * Lipschitz L == Norm._2.ofMatrix(a) */
public class LinearStateSpaceModel implements StateSpaceModel, Serializable {
  private final Tensor a;
  private final Tensor b;
  private final Tensor c;
  private final Tensor d;
  // private final Scalar L;

  public LinearStateSpaceModel(Tensor a, Tensor b, Tensor c, Tensor d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    // L = Norm._2.ofMatrix(a);
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    return a.dot(x).add(b.dot(u));
  }

  public Tensor output(Tensor x, Tensor u) {
    return c.dot(x).add(d.dot(u));
  }
}
