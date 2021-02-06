// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.img.Hue;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.img.StrictColorDataIndexed;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.sca.Mod;

/* package */ enum HueColorData {
  ;
  private static final Mod MOD = Mod.function(1);

  public static ColorDataIndexed of(int max, int sep) {
    Tensor tensor = Tensors.reserve(max * sep);
    Scalar goldenAngle = RealScalar.of(0.38196601125010515180);
    Scalar offset = RealScalar.of(0.66);
    Tensor sats = Subdivide.of(1.0, 0.2, sep - 1);
    for (int index = 0; index < max; ++index) {
      for (Tensor sat : sats)
        tensor.append(ColorFormat.toVector(Hue.of(offset.number().doubleValue(), ((Scalar) sat).number().doubleValue(), 1.0, 1.0)));
      offset = MOD.apply(offset.add(goldenAngle));
    }
    return StrictColorDataIndexed.of(tensor);
  }

  public static void main(String[] args) throws IOException {
    ColorDataIndexed colorDataIndexed = of(10, 5);
    Tensor tensor = Range.of(0, colorDataIndexed.length()).map(Tensors::of).map(colorDataIndexed);
    tensor = ImageResize.nearest(tensor, 10);
    Export.of(HomeDirectory.Pictures("huecolordata.png"), tensor);
  }
}
