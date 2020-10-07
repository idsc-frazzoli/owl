package ch.ethz.idsc.sophus.app.bdn;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.img.Hue;
import ch.ethz.idsc.tensor.img.StrictColorDataIndexed;
import ch.ethz.idsc.tensor.sca.Mod;

public enum HueColorData {
  ;
  private static final Mod MOD = Mod.function(1);

  public static ColorDataIndexed of(int max, int sep) {
    Tensor tensor = Tensors.reserve(max * sep);
    Scalar goldenAngle = RealScalar.of(0.38196601125010515180);
    Scalar offset = RealScalar.of(0.66);
    Tensor sats = Subdivide.of(1.0, 0.3, sep - 1);
    for (int index = 0; index < max; ++index) {
      for (Tensor sat : sats)
        tensor.append(ColorFormat.toVector(Hue.of(offset.number().doubleValue(), sat.Get().number().doubleValue(), 1.0, 1.0)));
      offset = MOD.apply(offset.add(goldenAngle));
    }
    return StrictColorDataIndexed.of(tensor);
  }
}
