// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.sophus.gds.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.ArrayPlotRender;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.img.ColorDataGradient;

/* package */ enum HilbertLevelImage {
  ;
  public static BufferedImage of(ManifoldDisplay geodesicDisplay, Tensor sequence, int res, ColorDataGradient colorDataGradient, int max) {
    TensorUnaryOperator tuo = IterativeGenesis.counts(geodesicDisplay.hsManifold(), sequence, max);
    int sequence_length = IterativeGenesis.values().length;
    Tensor fallback = ConstantArray.of(DoubleScalar.INDETERMINATE, sequence_length);
    GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay.geodesicArrayPlot();
    Tensor wgs = geodesicArrayPlot.raster(res, tuo, fallback);
    ArrayPlotRender arrayPlotRender = StaticHelper.arrayPlotFromTensor(wgs, 1, false, colorDataGradient);
    return StaticHelper.fuseImages(geodesicDisplay, arrayPlotRender, res, sequence_length);
  }
}
