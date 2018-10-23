// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.red.Norm;

/** a group element of SE(3) is represented as a 4x4 affine transformation matrix
 * 
 * an element of the algebra se(3) is represented as a vector of length 6
 * 
 * from Lie Groups for 2D and 3D Transformations by Ethan Eade */
public enum Se3Exponential implements LieExponential {
  INSTANCE;
  // ---
  private static final Tensor ID3 = IdentityMatrix.of(3);

  @Override // from LieExponential
  public Tensor exp(Tensor u_w) {
    Tensor u = u_w.get(0);
    Tensor w = u_w.get(1);
    Scalar theta = Norm._2.ofVector(w);
    Tensor wx = Cross.of(w);
    Tensor wx2 = wx.dot(wx);
    Se3Numerics se3Numerics = new Se3Numerics(theta);
    Tensor R = ID3.add(wx.multiply(se3Numerics.A)).add(wx2.multiply(se3Numerics.B));
    Tensor V = ID3.add(wx.multiply(se3Numerics.B)).add(wx2.multiply(se3Numerics.C));
    Tensor Vu = V.dot(u);
    // TODO use Se3Utils
    return Tensors.of( //
        Join.of(R.get(0), Vu.extract(0, 1)), //
        Join.of(R.get(1), Vu.extract(1, 2)), //
        Join.of(R.get(2), Vu.extract(2, 3)), //
        Tensors.vector(0, 0, 0, 1));
  }

  @Override // from LieExponential
  public Tensor log(Tensor g) {
    Tensor R = Tensor.of(g.extract(0, 3).stream().map(row -> row.extract(0, 3)));
    Tensor wx = Rodrigues.logMatrix(R);
    Tensor w = Tensors.of(wx.Get(2, 1), wx.Get(0, 2), wx.Get(1, 0)); // copied from Rodrigues
    Scalar theta = Norm._2.ofVector(w);
    Tensor wx2 = wx.dot(wx);
    Se3Numerics se3Numerics = new Se3Numerics(theta);
    Tensor Vi = ID3.subtract(wx.multiply(RationalScalar.HALF)).add(wx2.multiply(se3Numerics.D));
    Tensor t = g.get(Tensor.ALL, 3).extract(0, 3);
    return Tensors.of(Vi.dot(t), w);
  }
}
