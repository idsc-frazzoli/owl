// http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/index.htm
// adapted by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;

// TODO TENSOR V068 obsolete
public enum QuaternionToRotationMatrix {
  ;
  /** @param wxyz vector of length 4, does not have to have unit length
   * @return */
  public static Tensor of(Tensor wxyz) {
    return of(wxyz.Get(0), wxyz.Get(1), wxyz.Get(2), wxyz.Get(3));
  }

  /** @param re
   * @param im
   * @param jm
   * @param km
   * @return orthogonal 3x3 matrix */
  public static Tensor of(Scalar re, Scalar im, Scalar jm, Scalar km) {
    double q_w = re.number().doubleValue();
    double q_x = im.number().doubleValue();
    double q_y = jm.number().doubleValue();
    double q_z = km.number().doubleValue();
    double sqw = q_w * q_w;
    double sqx = q_x * q_x;
    double sqy = q_y * q_y;
    double sqz = q_z * q_z;
    // inverse square length is only required if Quaternion is not already normalized
    double inv = 1 / (sqx + sqy + sqz + sqw);
    if (!Double.isFinite(inv))
      throw TensorRuntimeException.of(re, im, jm, km);
    double m00 = (+sqx - sqy - sqz + sqw) * inv; // since sqw + sqx + sqy + sqz =1 / inv*inv
    double m11 = (-sqx + sqy - sqz + sqw) * inv;
    double m22 = (-sqx - sqy + sqz + sqw) * inv;
    double tmp1 = q_x * q_y;
    double tmp2 = q_z * q_w;
    double m10 = (tmp1 + tmp2) * inv;
    m10 += m10;
    double m01 = (tmp1 - tmp2) * inv;
    m01 += m01;
    tmp1 = q_x * q_z;
    tmp2 = q_y * q_w;
    double m20 = (tmp1 - tmp2) * inv;
    m20 += m20;
    double m02 = (tmp1 + tmp2) * inv;
    m02 += m02;
    tmp1 = q_y * q_z;
    tmp2 = q_x * q_w;
    double m21 = (tmp1 + tmp2) * inv;
    m21 += m21;
    double m12 = (tmp1 - tmp2) * inv;
    m12 += m12;
    return Tensors.matrix(new Number[][] { //
        { m00, m01, m02 }, //
        { m10, m11, m12 }, //
        { m20, m21, m22 } });
  }
}
