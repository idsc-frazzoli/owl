//// code by jph, ob
// package ch.ethz.idsc.sophus.app.filter;
//
// import java.awt.Graphics2D;
//
// import javax.swing.JToggleButton;
//
// import ch.ethz.idsc.owl.gui.win.GeometricLayer;
// import ch.ethz.idsc.sophus.flt.ga.NonuniformFixedIntervalGeodesicCenter;
// import ch.ethz.idsc.sophus.flt.ga.NonuniformFixedIntervalGeodesicCenterFilter;
// import ch.ethz.idsc.sophus.flt.ga.NonuniformFixedRadiusGeodesicCenter;
// import ch.ethz.idsc.sophus.flt.ga.NonuniformFixedRadiusGeodesicCenterFilter;
// import ch.ethz.idsc.sophus.gds.Se2GeodesicDisplay;
// import ch.ethz.idsc.tensor.RationalScalar;
// import ch.ethz.idsc.tensor.Tensor;
// import ch.ethz.idsc.tensor.Tensors;
//
/// * package */ class NonuniformGeodesicCenterFilterDemo extends AbstractNavigableMapDemo {
// private Tensor refined = Tensors.empty();
// protected final JToggleButton jToggleFixedRadius = new JToggleButton("fixedRadius");
// // interval manuell gekoppelt an sampling frequency
// protected final long samplingFrequency = 20;
//
// public NonuniformGeodesicCenterFilterDemo() {
// jToggleFixedRadius.setSelected(true);
// timerFrame.jToolBar.add(jToggleFixedRadius);
// setGeodesicDisplay(Se2GeodesicDisplay.INSTANCE);
// // ---
// updateStateTime();
// }
//
// @Override // from RenderInterface
// protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
// // interval is either: radius length or muliplicity of sampling frequency - depending on filter choice
// if (jToggleFixedRadius.isSelected()) {
// NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = NonuniformFixedRadiusGeodesicCenter.of(geodesicDisplay().geodesicInterface());
// refined = Tensor.of(NonuniformFixedRadiusGeodesicCenterFilter.of(nonuniformFixedRadiusGeodesicCenter, spinnerRadius.getValue())
// .apply(navigableMapStateTime()).values().stream());
// } else {
// NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = NonuniformFixedIntervalGeodesicCenter
// .of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue().get());
// refined = Tensor.of(
// NonuniformFixedIntervalGeodesicCenterFilter.of(nonuniformFixedIntervalGeodesicCenter, RationalScalar.of(spinnerRadius.getValue(), samplingFrequency))
// .apply(navigableMapStateTime()).values().stream());
// }
// return refined;
// }
//
// public static void main(String[] args) {
// new NonuniformGeodesicCenterFilterDemo().setVisible(1000, 800);
// }
// }
