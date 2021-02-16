// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JSlider;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.flt.WindowSideExtrapolation;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanFIRnFilter;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanIIRnFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.flt.ga.GeodesicFIRnFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMeans;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.opt.GeodesicCausalFilters;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

/* package */ class GeodesicCausalFilterDemo extends AbstractDatasetKernelDemo {
  protected final SpinnerLabel<GeodesicCausalFilters> spinnerCausalFilter = new SpinnerLabel<>();
  /** parameter to blend extrapolation with measurement */
  private final JSlider jSlider = new JSlider(1, 999, 500);

  public GeodesicCausalFilterDemo() {
    super(GeodesicDisplays.SE2_ONLY, GokartPoseDataV2.INSTANCE);
    {
      spinnerCausalFilter.setList(Arrays.asList(GeodesicCausalFilters.values()));
      spinnerCausalFilter.setValue(GeodesicCausalFilters.BIINVARIANT_MEAN_IIR);
      spinnerCausalFilter.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "smoothing kernel");
      spinnerCausalFilter.addSpinnerListener(value -> updateState());
    }
    jSlider.setPreferredSize(new Dimension(500, 28));
    // ---
    timerFrame.jToolBar.add(jSlider);
    // ---
    updateState();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final int radius = spinnerRadius.getValue();
    if (0 < radius) {
      ScalarUnaryOperator windowFunctions = spinnerKernel.getValue().get();
      Se2BiinvariantMeans se2BiinvariantMean = Se2BiinvariantMeans.FILTER;
      Geodesic geodesicInterface = Se2Geodesic.INSTANCE;
      TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, windowFunctions);
      // ---
      GeodesicCausalFilters geodesicCausalFilters = spinnerCausalFilter.getValue();
      TensorUnaryOperator tensorUnaryOperator = null;
      switch (geodesicCausalFilters) {
      case GEODESIC_FIR:
        tensorUnaryOperator = GeodesicFIRnFilter.of(geodesicExtrapolation, geodesicInterface, radius, alpha());
        break;
      case GEODESIC_IIR:
        tensorUnaryOperator = GeodesicIIRnFilter.of(geodesicExtrapolation, geodesicInterface, radius, alpha());
        break;
      case BIINVARIANT_MEAN_FIR:
        tensorUnaryOperator = //
            BiinvariantMeanFIRnFilter.of(se2BiinvariantMean, WindowSideExtrapolation.of(windowFunctions), Se2Geodesic.INSTANCE, radius, alpha());
        break;
      case BIINVARIANT_MEAN_IIR:
        tensorUnaryOperator = //
            BiinvariantMeanIIRnFilter.of(se2BiinvariantMean, WindowSideExtrapolation.of(windowFunctions), Se2Geodesic.INSTANCE, radius, alpha());
        break;
      }
      return tensorUnaryOperator.apply(control());
    }
    return control();
  }

  private Scalar alpha() {
    return RationalScalar.of(jSlider.getValue(), 1000);
  }

  @Override
  protected String plotLabel() {
    return super.plotLabel() + " " + alpha();
  }

  public static void main(String[] args) {
    new GeodesicCausalFilterDemo().setVisible(1000, 800);
  }
}