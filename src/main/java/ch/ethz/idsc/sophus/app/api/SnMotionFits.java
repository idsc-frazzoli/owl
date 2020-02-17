// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.sn.SnMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.RigidMotionFit;
import ch.ethz.idsc.tensor.sca.Chop;

/** RMF(p,t,w)[x] == w.t for w = IDC(p,x) */
public enum SnMotionFits {
  LINEAR() {
    @Override
    public Tensor map(Tensor origin, Tensor target, Tensor weights, Tensor p) {
      return SN_MEAN.mean(target, weights);
    }
  }, //
  RIGID() {
    @Override
    public Tensor map(Tensor origin, Tensor target, Tensor weights, Tensor p) {
      RigidMotionFit rigidMotionFit = RigidMotionFit.of(origin, target, weights);
      return rigidMotionFit.rotation().dot(p);
    }
  }, //
  ;

  static final SnMean SN_MEAN = new SnMean(Chop._05);

  public abstract Tensor map(Tensor origin, Tensor target, Tensor weights, Tensor p);
}
