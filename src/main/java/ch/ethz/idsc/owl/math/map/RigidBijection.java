// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Tensor;

/** every rigid transformation is a bijective mapping */
public interface RigidBijection extends Bijection {
  /** @return 3x3 matrix of rigid forward transformation at given scalar parameter */
  Tensor forward_se2();
}
