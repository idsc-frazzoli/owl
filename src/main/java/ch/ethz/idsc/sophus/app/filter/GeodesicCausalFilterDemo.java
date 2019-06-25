// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LieGroupCausalFilters;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.flt.WindowSideExtrapolation;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanFIRnFilter;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanIIRnFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.flt.ga.GeodesicFIRnFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.flt.ts.TangentSpaceFIRnFilter;
import ch.ethz.idsc.sophus.flt.ts.TangentSpaceIIRnFilter;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO OB adapt symLinkImages to new filter structure, see use of BufferedImageSupplier
/* package */ class GeodesicCausalFilterDemo extends DatasetKernelDemo {
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
      TensorUnaryOperator cf = null;
      switch (lgcf) {
      case GEODESIC_FIR:
        cf = GeodesicFIRnFilter.of(geodesicExtrapolation, geodesicInterface, radius, alpha());
        break;
      case GEODESIC_IIR:
        cf = GeodesicIIRnFilter.of(geodesicExtrapolation, geodesicInterface, radius, alpha());
        break;
      case TANGENT_SPACE_FIR:
        cf = TangentSpaceFIRnFilter.of( //
            Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, WindowSideExtrapolation.of(smoothingKernel), geodesicInterface, radius, alpha());
        break;
      case TANGENT_SPACE_IIR:
        cf = TangentSpaceIIRnFilter.of( //
            Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, WindowSideExtrapolation.of(smoothingKernel), geodesicInterface, radius, alpha());
        break;
      case BIINVARIANT_MEAN_FIR:
        cf = BiinvariantMeanFIRnFilter.of(se2BiinvariantMean, WindowSideExtrapolation.of(smoothingKernel), Se2Geodesic.INSTANCE, radius, alpha());
        break;
      case BIINVARIANT_MEAN_IIR:
        cf = BiinvariantMeanIIRnFilter.of(se2BiinvariantMean, WindowSideExtrapolation.of(smoothingKernel), Se2Geodesic.INSTANCE, radius, alpha());
        break;
      }
      return cf.apply(control());
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