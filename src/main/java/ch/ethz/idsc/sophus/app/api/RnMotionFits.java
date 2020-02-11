// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.RigidMotionFit;

/** RMF(p,t,w)[x] == w.t for w = IDC(p,x) */
public enum RnMotionFits {
  RIGID() {
    @Override
    public Tensor map(Tensor points, Tensor target, Tensor weights, Tensor p) {
      return RigidMotionFit.of(points, target, weights).apply(p);
    }
  }, //
  LINEAR() {
    @Override
    public Tensor map(Tensor points, Tensor target, Tensor weights, Tensor p) {
      return weights.dot(target);
    }
  }, //
  ;

  public abstract Tensor map(Tensor points, Tensor target, Tensor weights, Tensor p);
}
