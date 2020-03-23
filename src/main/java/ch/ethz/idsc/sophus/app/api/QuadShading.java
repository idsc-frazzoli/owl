// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.VectorAngle;
import ch.ethz.idsc.tensor.sca.Clips;

public enum QuadShading {
  ANGLE() {
    @Override
    public Scalar map(Tensor po, Tensor p0, Tensor p1, Tensor pd) {
      Optional<Scalar> optional = VectorAngle.of(p0.subtract(po), p1.subtract(po));
      return optional.map(Pi.VALUE::under).orElse(RealScalar.ZERO);
      // return optional.isPresent() //
      // ? optional.get().divide(Pi.VALUE)
      // : RealScalar.ZERO;
    }
  }, //
  VOLUME() {
    @Override
    public Scalar map(Tensor po, Tensor p0, Tensor p1, Tensor pd) {
      Scalar scalar = Norm._2.ofVector(Cross.of(p0.subtract(po), p1.subtract(po))).divide(RealScalar.of(10.0));
      return Clips.unit().apply(scalar);
    }
  }, //
  ;

  public abstract Scalar map(Tensor po, Tensor p0, Tensor p1, Tensor pd);
}
