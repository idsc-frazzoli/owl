// code by jph
package ch.ethz.idsc.owl.math.noise;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.UnitStep;

enum R2NoisePlot {
  ;
  // ---
  private static final NativeContinuousNoise NOISE = SimplexContinuousNoise.FUNCTION;
  // PolyNoise.of(SimplexNoise.FUNCTION, new double[] { 1, 0 }, new double[] { .3, 3 });
  private static final int RES = 512;
  private static final Tensor RE = Subdivide.of(0, 25, RES - 1);
  private static final Tensor IM = Subdivide.of(0, 25, RES - 1);
  @SuppressWarnings("unused")
  private static final Clip CLIP = Clips.unit();

  private static Scalar function(int x, int y) {
    return UnitStep.of(DoubleScalar.of(NOISE.at( //
        RE.Get(x).number().doubleValue(), //
        IM.Get(y).number().doubleValue())).subtract(RealScalar.of(0.9)));
  }

  public static void main(String[] args) throws Exception {
    Tensor matrix = Tensors.matrix(R2NoisePlot::function, RES, RES);
    Export.of(HomeDirectory.Pictures("perlinnoise.png"), //
        ArrayPlot.of(matrix, ColorDataGradients.COPPER));
  }
}
