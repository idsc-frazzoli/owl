// code by ob
package ch.ethz.idsc.sophus.lie.st;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Tensor;

/** (n+1)-dimensional Scaling and Translations group
 * 
 * "Exponential Barycenters of the Canonical Cartan Connection and Invariant Means on Lie Groups"
 * by Xavier Pennec, Vincent Arsigny, p.25, Section 4.1:
 * "The group of scalings and translations in n-D. The study of this (quite) simple group
 * is relevant in the context of this work, because it is one of the most simple cases of
 * non-compact and non-commutative Lie groups which does not possess any bi-invariant Rie-
 * mannian metric. This group has many of the properties of rigid-body or affine
 * transformations, but with only n + 1 degrees of freedom, which simplifies greatly the
 * computations, and allows a direct 2D geometric visualization in the plane for n = 1.
 * For these reasons, this is a highly pedagogical case." */
public enum StGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public StGroupElement element(Tensor lambda_t) {
    return new StGroupElement(lambda_t);
  }
}
