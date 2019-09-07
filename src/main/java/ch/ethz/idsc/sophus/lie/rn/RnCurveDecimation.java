// code by jph
package ch.ethz.idsc.sophus.lie.rn;

import ch.ethz.idsc.sophus.crv.CurveDecimation;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** Quote from Wikipedia:
 * The algorithm is widely used in robotics to perform simplification and denoising
 * of range data acquired by a rotating range scanner.
 * In this field it is known as the split-and-merge algorithm and is attributed to Duda and Hart.
 * 
 * The expected complexity of this algorithm is O(n log n).
 * However, the worst-case complexity is O(n^2). */
public enum RnCurveDecimation {
  ;
  /** @param dimensions
   * @param epsilon
   * @return */
  public static TensorUnaryOperator of(Scalar epsilon) {
    return CurveDecimation.of(RnGroup.INSTANCE, tensor -> tensor, epsilon);
  }
}
