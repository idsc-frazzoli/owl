// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;

/** Reference:
 * "Riemannian Geometric Statistics in Medical Image Analysis", 2020
 * Edited by Pennec, Sommer, Fletcher, p. 79 */
public enum SpdExponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Tensor exp(Tensor x) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(x);
    Tensor a = eigensystem.vectors();
    return LinearSolve.of(a, eigensystem.values().map(Exp.FUNCTION).pmul(a));
  }

  @Override // from LieExponential
  public Tensor log(Tensor g) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(g);
    Tensor a = eigensystem.vectors();
    return LinearSolve.of(a, eigensystem.values().map(Log.FUNCTION).pmul(a));
  }
}
