// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.sophus.lie.LinearGroup;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.red.Norm;

/** a group element of SE(3) is represented as a 4x4 affine transformation matrix
 * 
 * an element of the algebra se(3) is represented as a 2 x 3 matrix of the form
 * {{vx, vy, vz}, {wx, wy, wz}}
 * 
 * from "Lie Groups for 2D and 3D Transformations" by Ethan Eade
 * http://ethaneade.com/
 * 
 * @see LinearGroup
 * @see LieGroupElement */
public enum Se3Exponential implements LieExponential {
  INSTANCE;
  // ---
  private static final Tensor ID3 = IdentityMatrix.of(3);

  @Override // from LieExponential
  public Tensor exp(Tensor u_w) {
    Tensor u = u_w.get(0); // translation
    Tensor w = u_w.get(1); // rotation
    Scalar theta = Norm._2.ofVector(w);
    Tensor wx = Cross.skew3(w);
    Tensor wx2 = wx.dot(wx);
    Se3Numerics se3Numerics = new Se3Numerics(theta);
    Tensor R = ID3.add(wx.multiply(se3Numerics.A)).add(wx2.multiply(se3Numerics.B));
    Tensor V = ID3.add(wx.multiply(se3Numerics.B)).add(wx2.multiply(se3Numerics.C));
    Tensor Vu = V.dot(u);
    return Se3Utils.toMatrix4x4(R, Vu);
  }

  @Override // from LieExponential
  public Tensor log(Tensor g) {
    Tensor R = Se3Utils.rotation(g);
    Tensor wx = Rodrigues.logMatrix(R);
    Tensor w = Tensors.of(wx.Get(2, 1), wx.Get(0, 2), wx.Get(1, 0)); // copied from Rodrigues
    Scalar theta = Norm._2.ofVector(w);
    Tensor wx2 = wx.dot(wx);
    Se3Numerics se3Numerics = new Se3Numerics(theta);
    Tensor Vi = ID3.subtract(wx.multiply(RationalScalar.HALF)).add(wx2.multiply(se3Numerics.D));
    Tensor t = Se3Utils.translation(g);
    return Tensors.of(Vi.dot(t), w);
  }
}
