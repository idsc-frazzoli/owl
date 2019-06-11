// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.misc.CurveCurvatureRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.app.util.StandardMenu;
import ch.ethz.idsc.sophus.crv.subdiv.MSpline3CurveSubdivision;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Mean;

// TODO JPH redundant to CurveSubdivisionDemo
public class BiinvariantMeanSubdivisionDemo extends CurvatureDemo {
  private static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] //
  { { 50, 0, 100 }, { 0, -50, 640 }, { 0, 0, 1 } });
  // ---
  private final SpinnerLabel<CurveSubdivisionSchemes> spinnerLabel = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerMagicC = new SpinnerLabel<>();
  private final JToggleButton jToggleLine = new JToggleButton("line");
  private final JToggleButton jToggleCyclic = new JToggleButton("cyclic");
  private final JToggleButton jToggleSymi = new JToggleButton("graph");
  // private final PathRender lineRender = new PathRender(new Color(0, 255, 0, 128));
  private final JToggleButton jToggleTest = new JToggleButton("active");

  public BiinvariantMeanSubdivisionDemo() {
    super(GeodesicDisplays.SE2C_SE2_R2);
    Tensor control = null;
    {
      Tensor move = Tensors.fromString( //
          "{{1,0,0},{1,0,0},{2,0,2.5708},{1,0,2.1},{1.5,0,0},{2.3,0,-1.2},{1.5,0,0},{4,0,3.14159},{2,0,3.14159},{2,0,0}}");
      move = Tensor.of(move.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))));
      Tensor init = Tensors.vector(0, 0, 2.1);
      control = DubinsGenerator.of(init, move);
      control = Tensors.fromString("{{0,0,0},{1,0,0},{2,0,0},{3,1,0},{4,1,0},{5,0,0},{6,0,0},{7,0,0}}").multiply(RealScalar.of(2));
    }
    setControlPointsSe2(control);
    timerFrame.jToolBar.addSeparator();
    {
      JButton jButton = new JButton("load");
      List<String> list = Arrays.asList("ducttape/20180514.csv", "tires/20190116.csv", "tires/20190117.csv");
      Supplier<StandardMenu> supplier = () -> new StandardMenu() {
        @Override
        protected void design(JPopupMenu jPopupMenu) {
          for (String string : list) {
            JMenuItem jMenuItem = new JMenuItem(string);
            jMenuItem.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent actionEvent) {
                Tensor tensor = ResourceData.of("/dubilab/controlpoints/" + string);
                tensor = Tensor.of(tensor.stream().map(row -> row.pmul(Tensors.vector(0.5, 0.5, 1))));
                Tensor center = Mean.of(tensor);
                center.set(RealScalar.ZERO, 2);
                tensor = Tensor.of(tensor.stream().map(row -> row.subtract(center)));
                setGeodesicDisplay(Se2GeodesicDisplay.INSTANCE);
                jToggleCyclic.setSelected(true);
                setControlPointsSe2(tensor);
              }
            });
            jPopupMenu.add(jMenuItem);
          }
        }
      };
      StandardMenu.bind(jButton, supplier);
      timerFrame.jToolBar.add(jButton);
    }
    // ---
    jToggleLine.setSelected(false);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    jToggleTest.setSelected(isPositioningEnabled());
    jToggleTest.addActionListener(actionEvent -> setPositioningEnabled(jToggleTest.isSelected()));
    timerFrame.jToolBar.add(jToggleTest);
    // ---
    timerFrame.jToolBar.addSeparator();
    addButtonDubins();
    // ---
    // jToggleCyclic.setSelected(true);
    timerFrame.jToolBar.add(jToggleCyclic);
    // ---
    jToggleSymi.setSelected(true);
    timerFrame.jToolBar.add(jToggleSymi);
    // ---
    spinnerLabel.setArray(CurveSubdivisionSchemes.values());
    spinnerLabel.setIndex(9);
    spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(150, 28), "scheme");
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRefine.setValue(6);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    spinnerMagicC.addSpinnerListener(value -> CurveSubdivisionHelper.MAGIC_C = value);
    spinnerMagicC.setList( //
        Tensors.fromString("{1/100, 1/10, 1/8, 1/6, 1/4, 1/3, 1/2, 2/3, 9/10, 99/100}").stream() //
            .map(Scalar.class::cast) //
            .collect(Collectors.toList()));
    spinnerMagicC.setValue(RationalScalar.HALF);
    spinnerMagicC.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    {
      JSlider jSlider = new JSlider(1, 999, 500);
      jSlider.setPreferredSize(new Dimension(360, 28));
      jSlider.addChangeListener(changeEvent -> //
      CurveSubdivisionHelper.MAGIC_C = RationalScalar.of(jSlider.getValue(), 1000));
      timerFrame.jToolBar.add(jSlider);
    }
    timerFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    // timerFrame.geometricComponent.addRenderInterfaceBackground(StaticHelper.GRID_RENDER);
  }

  @Override
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final CurveSubdivisionSchemes scheme = spinnerLabel.getValue();
    if (scheme.equals(CurveSubdivisionSchemes.DODGSON_SABIN))
      setGeodesicDisplay(R2GeodesicDisplay.INSTANCE);
    // ---
    if (jToggleSymi.isSelected()) {
      Optional<SymMaskImages> optional = SymMaskImages.get(scheme.name());
      if (optional.isPresent()) {
        BufferedImage image0 = optional.get().image0();
        graphics.drawImage(image0, 0, 0, null);
        BufferedImage image1 = optional.get().image1();
        graphics.drawImage(image1, image0.getWidth() + 1, 0, null);
      }
    }
    GraphicsUtil.setQualityHigh(graphics);
    // ---
    final boolean cyclic = jToggleCyclic.isSelected() || !scheme.isStringSupported();
    Tensor control = getGeodesicControlPoints();
    int levels = spinnerRefine.getValue();
    Tensor refined;
    renderControlPoints(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    {
      TensorUnaryOperator tensorUnaryOperator = new MSpline3CurveSubdivision(geodesicDisplay.biinvariantMean())::string;
      // create(function.apply(geodesicDisplay.geodesicInterface()), cyclic);
      refined = control;
      for (int level = 0; level < levels; ++level) {
        Tensor prev = refined;
        refined = tensorUnaryOperator.apply(refined);
        if (CurveSubdivisionHelper.isDual(scheme) && level % 2 == 1 && !cyclic && 1 < control.length()) {
          refined = Join.of( //
              Tensors.of(geodesicDisplay.geodesicInterface().split(control.get(0), prev.get(0), RationalScalar.HALF)), //
              refined, //
              Tensors.of(geodesicDisplay.geodesicInterface().split(Last.of(prev), Last.of(control), RationalScalar.HALF)) //
          );
        }
      }
    }
    // if (jToggleLine.isSelected()) {
    // TensorUnaryOperator tensorUnaryOperator = create(new BSpline1CurveSubdivision(geodesicDisplay.geodesicInterface()), cyclic);
    // lineRender.setCurve(Nest.of(tensorUnaryOperator, control, 8), cyclic).render(geometricLayer, graphics);
    // }
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    CurveCurvatureRender.of(render, cyclic, geometricLayer, graphics);
    if (levels < 5)
      renderPoints(geodesicDisplay, refined, geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BiinvariantMeanSubdivisionDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
