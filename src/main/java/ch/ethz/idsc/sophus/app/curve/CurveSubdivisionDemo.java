// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.InvertUnlessZero;

/* package */ class CurveSubdivisionDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = //
      ColorDataLists._097.cyclic().deriveWithAlpha(128 + 64);
  // private static final Tensor DUBILAB = //
  // ResourceData.of("/dubilab/controlpoints/eight/20180603.csv").multiply(RealScalar.of(.4)).unmodifiable();
  // ---
  private final SpinnerLabel<CurveSubdivisionSchemes> spinnerLabel = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerMagicC = new SpinnerLabel<>();
  private final JToggleButton jToggleBndy = new JToggleButton("bndy");
  private final JToggleButton jToggleCrvt = new JToggleButton("crvt");
  private final JToggleButton jToggleLine = new JToggleButton("line");
  private final JToggleButton jToggleCyclic = new JToggleButton("cyclic");
  private final JToggleButton jToggleSymi = new JToggleButton("graph");
  private final PathRender lineRender = new PathRender(new Color(0, 255, 0, 128));

  CurveSubdivisionDemo() {
    super(true, true, GeodesicDisplays.ALL);
    Tensor control = null;
    {
      Tensor move = Tensors.fromString( //
          "{{1,0,0},{1,0,0},{2,0,2.5708},{1,0,2.1},{1.5,0,0},{2.3,0,-1.2},{1.5,0,0},{4,0,3.14159},{2,0,3.14159},{2,0,0}}");
      move = Tensor.of(move.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))));
      Tensor init = Tensors.vector(0, 0, 2.1);
      control = DubinsGenerator.of(init, move);
    }
    setControl(control);
    // {
    // JButton jButton = new JButton("print");
    // jButton.addActionListener(actionEvent -> {
    // System.out.println(control);
    // // long now = System.currentTimeMillis();
    // File file = UserHome.file("" + jTextField.getText() + ".csv");
    // // File file = new File("src/main/resources/subdiv/se2", now + ".csv");
    // try {
    // Export.of(file, control.map(CsvFormat.strict()));
    // } catch (Exception exception) {
    // exception.printStackTrace();
    // }
    // });
    // timerFrame.jToolBar.add(jButton);
    // }
    // ---
    jToggleBndy.setSelected(true);
    timerFrame.jToolBar.add(jToggleBndy);
    // ---
    jToggleCrvt.setSelected(false);
    timerFrame.jToolBar.add(jToggleCrvt);
    // ---
    jToggleLine.setSelected(false);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    timerFrame.jToolBar.addSeparator();
    addButtonDubins();
    // ---
    timerFrame.jToolBar.add(jToggleCyclic);
    // ---
    jToggleSymi.setSelected(true);
    timerFrame.jToolBar.add(jToggleSymi);
    // ---
    spinnerLabel.setArray(CurveSubdivisionSchemes.values());
    spinnerLabel.setIndex(2);
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
      jSlider.setPreferredSize(new Dimension(500, 28));
      jSlider.addChangeListener(changeEvent -> //
      CurveSubdivisionHelper.MAGIC_C = RationalScalar.of(jSlider.getValue(), 1000));
      timerFrame.jToolBar.add(jSlider);
    }
    timerFrame.geometricComponent.addRenderInterfaceBackground(StaticHelper.GRID_RENDER);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final CurveSubdivisionSchemes scheme = spinnerLabel.getValue();
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
    Function<GeodesicInterface, CurveSubdivision> function = spinnerLabel.getValue().function;
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    final boolean cyclic = jToggleCyclic.isSelected() || !scheme.isStringSupported();
    Tensor control = control();
    if (jToggleBndy.isSelected() && !cyclic && 1 < control.length()) {
      switch (scheme) {
      case BSPLINE2:
      case BSPLINE4:
      case BSPLINE4S3:
      case BSPLINE4S2:
        control = Join.of( //
            control.extract(0, 1), //
            control, //
            control.extract(control.length() - 1, control.length()));
        break;
      default:
        break;
      }
    }
    int levels = spinnerRefine.getValue();
    final Tensor refined;
    renderControlPoints(geometricLayer, graphics);
    {
      TensorUnaryOperator tensorUnaryOperator = create(function.apply(geodesicDisplay.geodesicInterface()), cyclic);
      refined = Nest.of(tensorUnaryOperator, control, levels);
    }
    if (jToggleLine.isSelected()) {
      TensorUnaryOperator tensorUnaryOperator = create(new BSpline1CurveSubdivision(geodesicDisplay.geodesicInterface()), cyclic);
      lineRender.setCurve(Nest.of(tensorUnaryOperator, control, 8), cyclic).render(geometricLayer, graphics);
    }
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    renderCurve(render, cyclic, geometricLayer, graphics);
    if (jToggleCrvt.isSelected()) {
      graphics.setStroke(new BasicStroke(1.25f));
      Tensor matrix = geometricLayer.getMatrix();
      geometricLayer.pushMatrix(Inverse.of(matrix));
      geometricLayer.pushMatrix(Tensors.fromString("{{1,0,0},{0,-50,100},{0,0,1}}"));
      Tensor points = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
      {
        graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
        Tensor curvature = SignedCurvature2D.string(points);
        Tensor domain = Range.of(0, curvature.length());
        graphics.draw(geometricLayer.toPath2D(Transpose.of(Tensors.of(domain, curvature))));
      }
      Tensor diffs = Differences.of(refined.get(Tensor.ALL, 2));
      {
        graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
        Tensor domain = Range.of(0, diffs.length());
        graphics.draw(geometricLayer.toPath2D(Transpose.of(Tensors.of(domain, diffs))));
      }
      Tensor arclen = Tensor.of(Differences.of(points).stream().map(Norm._2::ofVector));
      {
        graphics.setColor(COLOR_DATA_INDEXED.getColor(2));
        Tensor domain = Range.of(0, arclen.length());
        graphics.draw(geometricLayer.toPath2D(Transpose.of(Tensors.of(domain, arclen))));
      }
      {
        graphics.setColor(COLOR_DATA_INDEXED.getColor(3));
        Tensor div = diffs.pmul(arclen.map(InvertUnlessZero.FUNCTION));
        Tensor domain = Range.of(0, div.length());
        graphics.draw(geometricLayer.toPath2D(Transpose.of(Tensors.of(domain, div.multiply(RealScalar.of(-1))))));
      }
      geometricLayer.popMatrix();
      geometricLayer.popMatrix();
      graphics.setStroke(new BasicStroke(1f));
    }
    if (levels < 5)
      renderPoints(geometricLayer, graphics, refined);
  }

  private static TensorUnaryOperator create(CurveSubdivision curveSubdivision, boolean cyclic) {
    return cyclic //
        ? curveSubdivision::cyclic
        : curveSubdivision::string;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new CurveSubdivisionDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
