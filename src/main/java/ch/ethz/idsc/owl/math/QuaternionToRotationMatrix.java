// http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/index.htm
// adapted by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// TODO use Scalar instead of double!
public enum QuaternionToRotationMatrix {
  ;
  /** @param wxyz vector of length 4, does not have to have unit length
   * @return */
  public static Tensor of(Tensor wxyz) {
    double q_w = wxyz.Get(0).number().doubleValue();
    double q_x = wxyz.Get(1).number().doubleValue();
    double q_y = wxyz.Get(2).number().doubleValue();
    double q_z = wxyz.Get(3).number().doubleValue();
    double sqw = q_w * q_w;
    double sqx = q_x * q_x;
    double sqy = q_y * q_y;
    double sqz = q_z * q_z;
    // invs (inverse square length) is only required if quaternion is not already normalized
    double invs = 1 / (sqx + sqy + sqz + sqw);
    double m00 = (sqx - sqy - sqz + sqw) * invs; // since sqw + sqx + sqy + sqz =1/invs*invs
    double m11 = (-sqx + sqy - sqz + sqw) * invs;
    double m22 = (-sqx - sqy + sqz + sqw) * invs;
    double tmp1 = q_x * q_y;
    double tmp2 = q_z * q_w;
    double m10 = 2.0 * (tmp1 + tmp2) * invs;
    double m01 = 2.0 * (tmp1 - tmp2) * invs;
    tmp1 = q_x * q_z;
    tmp2 = q_y * q_w;
    double m20 = 2.0 * (tmp1 - tmp2) * invs;
    double m02 = 2.0 * (tmp1 + tmp2) * invs;
    tmp1 = q_y * q_z;
    tmp2 = q_x * q_w;
    double m21 = 2.0 * (tmp1 + tmp2) * invs;
    double m12 = 2.0 * (tmp1 - tmp2) * invs;
    return Tensors.matrix(new Number[][] { //
        { m00, m01, m02 }, //
        { m10, m11, m12 }, //
        { m20, m21, m22 } });
  }
}
