// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.filter.BiinvariantFIRnFilter;
import ch.ethz.idsc.sophus.filter.BiinvariantIIRnFilter;
import ch.ethz.idsc.sophus.filter.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.filter.GeodesicFIRnFilter;
import ch.ethz.idsc.sophus.filter.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.filter.LieGroupCausalFilters;
import ch.ethz.idsc.sophus.filter.TangentSpaceFIRnFilter;
import ch.ethz.idsc.sophus.filter.TangentSpaceIIRnFilter;
import ch.ethz.idsc.sophus.group.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO OB adapt symLinkImages to new filter structure, see use of BufferedImageSupplier
/* package */ class GeodesicCausalFilterDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();
  protected final SpinnerLabel<LieGroupCausalFilters> spinnerCausalFilter = new SpinnerLabel<>();
  /** parameter to blend extrapolation with measurement */
  private final JSlider jSlider = new JSlider(1, 999, 500);

  public GeodesicCausalFilterDemo() {
    super(GeodesicDisplays.SE2_ONLY);
    {
      spinnerCausalFilter.setList(Arrays.asList(LieGroupCausalFilters.values()));
      spinnerCausalFilter.setValue(LieGroupCausalFilters.BIINVARIANT_MEAN_IIR);
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
      SmoothingKernel smoothingKernel = spinnerKernel.getValue();
      Se2BiinvariantMean se2BiinvariantMean = Se2BiinvariantMean.FILTER;
      GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
      TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
      // ---
      LieGroupCausalFilters lgcf = spinnerCausalFilter.getValue();
      switch (lgcf) {
      case GEODESIC_FIR:
        refined = GeodesicFIRnFilter.of(geodesicExtrapolation, geodesicInterface, radius, alpha()).apply(control());
        break;
      case GEODESIC_IIR:
        refined = GeodesicIIRnFilter.of(geodesicExtrapolation, geodesicInterface, radius, alpha()).apply(control());
        break;
      case TANGENT_SPACE_FIR:
        refined = TangentSpaceFIRnFilter.of(smoothingKernel, radius, alpha()).apply(control());
        break;
      case TANGENT_SPACE_IIR:
        refined = TangentSpaceIIRnFilter.of(smoothingKernel, radius, alpha()).apply(control());
        break;
      case BIINVARIANT_MEAN_FIR:
        refined = BiinvariantFIRnFilter.of(se2BiinvariantMean, smoothingKernel, radius, alpha()).apply(control());
        break;
      case BIINVARIANT_MEAN_IIR:
        refined = BiinvariantIIRnFilter.of(se2BiinvariantMean, smoothingKernel, radius, alpha()).apply(control());
        break;
      }
      return refined;
    }
    // TODO OB: I would like to have this shape with one filter for all different operators
    // if (0 < radius) {
    // ScalarUnaryOperator smoothingKernel = spinnerKernel.getValue();
    // // --
    // Map<LieGroupCausalFilters, TensorUnaryOperator> map = new EnumMap<>(LieGroupCausalFilters.class);
    // map.put(LieGroupCausalFilters.GEODESIC_FIR, GeodesicFIRnNEW.of(geodesicDisplay(), smoothingKernel, radius, alpha()));
    // map.put(LieGroupCausalFilters.GEODESIC_IIR, GeodesicIIRnNEW.of(geodesicDisplay(), smoothingKernel, radius, alpha()));
    // map.put(LieGroupCausalFilters.TANGENT_SPACE_FIR, TangentSpaceFIRnNEW.of(geodesicDisplay(), smoothingKernel, radius, alpha()));
    // map.put(LieGroupCausalFilters.TANGENT_SPACE_IIR, TangentSpaceIIRnNEW.of(geodesicDisplay(), smoothingKernel, radius, alpha()));
    // map.put(LieGroupCausalFilters.BIINVARIANT_MEAN_FIR, BiinvariantMeanFIRnNEW.of(geodesicDisplay(), smoothingKernel, radius, alpha()));
    // map.put(LieGroupCausalFilters.BIINVARIANT_MEAN_IIR, BiinvariantMeanIIRnNEW.of(geodesicDisplay(), smoothingKernel, radius, alpha()));
    // refined = GeodesicCausalFilter.of(map.get(spinnerCausalFilter.getValue())).apply(control());
    // return refined;
    // }
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
    AbstractDemo abstractDemo = new GeodesicCausalFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}