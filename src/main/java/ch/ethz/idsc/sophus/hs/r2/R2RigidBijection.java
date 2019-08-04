// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import ch.ethz.idsc.sophus.math.Bijection;
import ch.ethz.idsc.tensor.Tensor;

/** every rigid transformation is a bijective mapping */
public interface R2RigidBijection extends Bijection {
  /** @return 3x3 matrix of rigid forward transformation at given scalar parameter */
  Tensor forward_se2();
}
