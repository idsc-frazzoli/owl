// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sinc;

/** a group element of SE(3) is represented as a 4x4 affine transformation matrix
 * 
 * an element of the algebra se(3) is represented as a vector of length 6
 * 
 * from Lie Groups for 2D and 3D Transformations by Ethan Eade */
public enum Se3Exponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Tensor exp(Tensor u_w) {
    Tensor u = u_w.get(0);
    Tensor w = u_w.get(1);
    Scalar theta = Norm._2.ofVector(w);
    Scalar theta2 = theta.multiply(theta);
    // TODO check if theta2 == 0
    Scalar A = Sinc.FUNCTION.apply(theta);
    Scalar B = RealScalar.ONE.subtract(Cos.FUNCTION.apply(theta)).divide(theta2);
    Scalar C = RealScalar.ONE.subtract(A).divide(theta2);
    Tensor wx = Cross.of(w);
    Tensor wx2 = wx.dot(wx);
    Tensor R = IdentityMatrix.of(3).add(wx.multiply(A)).add(wx2.multiply(B));
    Tensor V = IdentityMatrix.of(3).add(wx.multiply(B)).add(wx2.multiply(C));
    Tensor Vu = V.dot(u);
    return Tensors.of( //
        Join.of(R.get(0), Vu.extract(0, 1)), //
        Join.of(R.get(1), Vu.extract(1, 2)), //
        Join.of(R.get(2), Vu.extract(2, 3)), //
        Tensors.vector(0, 0, 0, 1));
  }

  @Override // from LieExponential
  public Tensor log(Tensor g) {
    Tensor R = Tensor.of(g.extract(0, 3).stream().map(r -> r.extract(0, 3)));
    Tensor w = Rodrigues.log(R); // TODO inefficient
    Scalar theta = Norm._2.ofVector(w);
    Scalar theta2 = theta.multiply(theta);
    // TODO check if theta2 == 0
    Scalar A = Sinc.FUNCTION.apply(theta);
    Scalar B = RealScalar.ONE.subtract(Cos.FUNCTION.apply(theta)).divide(theta2);
    Scalar C = RealScalar.ONE.subtract(A).divide(theta2);
    Tensor wx = Cross.of(w);
    Tensor wx2 = wx.dot(wx);
    Scalar coeff = RealScalar.ONE.subtract(A.divide(B.multiply(RealScalar.of(2)))).divide(theta2);
    Tensor Vi = IdentityMatrix.of(3).subtract(wx.multiply(RationalScalar.HALF)).add(wx2.multiply(coeff));
    Tensor t = g.get(Tensor.ALL, 3).extract(0, 3);
    return Tensors.of(Vi.dot(t), w);
  }
}
