// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.filter.BiinvariantMeanFIRn;
import ch.ethz.idsc.sophus.filter.BiinvariantMeanIIRn;
import ch.ethz.idsc.sophus.filter.GeodesicCausalFilter;
import ch.ethz.idsc.sophus.filter.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.filter.GeodesicFIRn;
import ch.ethz.idsc.sophus.filter.GeodesicIIRn;
import ch.ethz.idsc.sophus.filter.LieGroupCausalFilters;
import ch.ethz.idsc.sophus.filter.TangentSpaceFIRn;
import ch.ethz.idsc.sophus.filter.TangentSpaceIIRn;
import ch.ethz.idsc.sophus.group.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

// TODO OB adapt symLinkImages to new filter structure, see use of BufferedImageSupplier
/* package */ class GeodesicCausalFilterDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();
  protected final SpinnerLabel<LieGroupCausalFilters> spinnerCausalFilter = new SpinnerLabel<>();
  /** parameter to blend extrapolation with measurement */
  private final JSlider jSlider = new JSlider(1, 999, 500);

  public GeodesicCausalFilterDemo() {
    {
      spinnerCausalFilter.setList(Arrays.asList(LieGroupCausalFilters.values()));
      spinnerCausalFilter.setValue(LieGroupCausalFilters.GEODESIC_IIR);
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
      GeodesicInterface geodesicInterface = geodesicDisplay().geodesicInterface();
      TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, spinnerKernel.getValue());
      // ---
      ScalarUnaryOperator smoothingKernel = spinnerKernel.getValue();
      Map<LieGroupCausalFilters, TensorUnaryOperator> map = new EnumMap<>(LieGroupCausalFilters.class);
      map.put(LieGroupCausalFilters.GEODESIC_FIR, GeodesicFIRn.of(geodesicExtrapolation, geodesicInterface, radius, alpha()));
      map.put(LieGroupCausalFilters.GEODESIC_IIR, GeodesicIIRn.of(geodesicExtrapolation, geodesicInterface, radius, alpha()));
      map.put(LieGroupCausalFilters.TANGENT_SPACE_FIR, TangentSpaceFIRn.of(geodesicDisplay(), smoothingKernel, radius, alpha()));
      map.put(LieGroupCausalFilters.TANGENT_SPACE_IIR, TangentSpaceIIRn.of(geodesicDisplay(), smoothingKernel, radius, alpha()));
      map.put(LieGroupCausalFilters.BIINVARIANT_MEAN_FIR, BiinvariantMeanFIRn.of(Se2BiinvariantMean.FILTER, smoothingKernel, radius, alpha()));
      map.put(LieGroupCausalFilters.BIINVARIANT_MEAN_IIR, BiinvariantMeanIIRn.of(Se2BiinvariantMean.FILTER, smoothingKernel, radius, alpha()));
      refined = GeodesicCausalFilter.of(map.get(spinnerCausalFilter.getValue())).apply(control());
      return refined;
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
    AbstractDemo abstractDemo = new GeodesicCausalFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}