// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LieGroupFilters;
import ch.ethz.idsc.sophus.app.util.BufferedImageSupplier;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

public class LieGroupFiltersDatasetDemo extends DatasetKernelDemo implements BufferedImageSupplier {
  private final SpinnerLabel<LieGroupFilters> spinnerFilters = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerConvolution = new SpinnerLabel<>();

  public LieGroupFiltersDatasetDemo() {
    super(GeodesicDisplays.SE2_R2);
    {
      spinnerFilters.setArray(LieGroupFilters.values());
      spinnerFilters.setIndex(0);
      spinnerFilters.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "filter type");
      spinnerFilters.addSpinnerListener(type -> updateState());
    }
    {
      spinnerConvolution.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
      spinnerConvolution.setIndex(0);
      spinnerConvolution.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "convolution");
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
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    SmoothingKernel smoothingKernel = spinnerKernel.getValue();
    LieGroupFilters lieGroupFilters = spinnerFilters.getValue();
    TensorUnaryOperator tensorUnaryOperator = //
        GeodesicDisplays.filter(geodesicDisplay, smoothingKernel, lieGroupFilters);
    return Nest.of( //
        CenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()), //
        control(), spinnerConvolution.getValue());
  }

  @Override // from BufferedImageSupplier
  public BufferedImage bufferedImage() {
    LieGroupFilters lieGroupFilters = spinnerFilters.getValue();
    switch (lieGroupFilters) {
    case GEODESIC:
      return GeodesicCenterSymLinkImage.of(spinnerKernel.getValue(), spinnerRadius.getValue()).bufferedImage();
    default:
      return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new LieGroupFiltersDatasetDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
