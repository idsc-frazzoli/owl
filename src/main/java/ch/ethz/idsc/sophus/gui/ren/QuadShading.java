// code by jph
package ch.ethz.idsc.sophus.gui.ren;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.red.VectorAngle;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ enum QuadShading {
  ANGLE {
    @Override
    public Scalar map(Tensor po, Tensor p0, Tensor p1, Tensor pd) {
      Optional<Scalar> optional = VectorAngle.of(p0.subtract(po), p1.subtract(po));
      return optional.map(Pi.VALUE::under).orElse(RealScalar.ZERO);
    }
  },
  VOLUME {
    private final Scalar factor = RealScalar.of(0.1);

    @Override
    public Scalar map(Tensor po, Tensor p0, Tensor p1, Tensor pd) {
      Scalar scalar = Vector2Norm.of(Cross.of(p0.subtract(po), p1.subtract(po))).multiply(factor);
      return Clips.unit().apply(scalar);
    }
  }, //
  ;

  public abstract Scalar map(Tensor po, Tensor p0, Tensor p1, Tensor pd);
}
