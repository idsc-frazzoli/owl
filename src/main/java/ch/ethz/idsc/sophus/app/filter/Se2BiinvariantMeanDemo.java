// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMeans;
import ch.ethz.idsc.sophus.lie.so2.So2FilterBiinvariantMean;
import ch.ethz.idsc.sophus.lie.so2.So2LinearBiinvariantMean;
import ch.ethz.idsc.sophus.lie.so2.So2PhongBiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

/** demo of {@link Se2BiinvariantMeans}
 * 
 * illustration of three ways to average the angular component:
 * {@link So2LinearBiinvariantMean}
 * {@link So2FilterBiinvariantMean}
 * {@link So2PhongBiinvariantMean} */
/* package */ class Se2BiinvariantMeanDemo extends AbstractDatasetKernelDemo {
  private final SpinnerLabel<Se2BiinvariantMeans> spinnerFilters = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerConvolution = new SpinnerLabel<>();

  public Se2BiinvariantMeanDemo() {
    super(GeodesicDisplays.SE2_ONLY, GokartPoseDataV2.INSTANCE);
    {
      spinnerFilters.setArray(Se2BiinvariantMeans.values());
      spinnerFilters.setIndex(0);
      spinnerFilters.addToComponentReduced(timerFrame.jToolBar, new Dimension(90, 28), "se2 biinvariant mean");
      spinnerFilters.addSpinnerListener(type -> updateState());
    }
    {
      spinnerConvolution.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerConvolution.setIndex(0);
      spinnerConvolution.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "convolution");
      spinnerConvolution.addSpinnerListener(type -> updateState());
    }
    // ---
    updateState();
  }

  @Override
  protected void updateState() {
    super.updateState();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // GeodesicDisplay geodesicDisplay = geodesicDisplay();
    ScalarUnaryOperator smoothingKernel = spinnerKernel.getValue().get();
    Se2BiinvariantMeans se2BiinvariantMean = spinnerFilters.getValue();
    TensorUnaryOperator tensorUnaryOperator = BiinvariantMeanCenter.of(se2BiinvariantMean, smoothingKernel);
    return Nest.of( //
        CenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()), //
        control(), spinnerConvolution.getValue());
  }

  public static void main(String[] args) {
    new Se2BiinvariantMeanDemo().setVisible(1000, 800);
  }
}
