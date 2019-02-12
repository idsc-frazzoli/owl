// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

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
import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.sophus.planar.SignedCurvature2D;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.InvertUnlessZero;

/* package */ class CurveSubdivisionDemo extends ControlPointsDemo {
  private static final Stroke PLOT_STROKE = new BasicStroke(1.5f);
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
    if (jToggleCrvt.isSelected() && 1 < refined.length()) {
      VisualSet visualSet = new VisualSet();
      Tensor points = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
      {
        Tensor curvature = SignedCurvature2D.string(points);
        Tensor domain = Range.of(0, curvature.length());
        VisualRow visualRow = visualSet.add(domain, curvature);
        visualRow.setLabel("curvature");
        visualRow.setStroke(PLOT_STROKE);
      }
      Tensor diffs = Differences.of(refined);
      {
        Tensor domain = Range.of(0, diffs.length());
        VisualRow visualRow = visualSet.add(domain, Tensor.of(diffs.stream().map(ArcTan2D::of)));
        visualRow.setLabel("arcTan[dx,dy]");
        visualRow.setStroke(PLOT_STROKE);
      }
      {
        Tensor domain = Range.of(0, refined.length());
        VisualRow visualRow = visualSet.add(domain, refined.get(Tensor.ALL, 2));
        visualRow.setLabel("phase");
        visualRow.setStroke(PLOT_STROKE);
      }
      Tensor phase = diffs.get(Tensor.ALL, 2);
      {
        Tensor domain = Range.of(0, phase.length());
        VisualRow visualRow = visualSet.add(domain, phase);
        visualRow.setLabel("phase diff");
        visualRow.setStroke(PLOT_STROKE);
      }
      Tensor arclen = Tensor.of(Differences.of(points).stream().map(Norm._2::ofVector));
      {
        Tensor domain = Range.of(0, arclen.length());
        VisualRow visualRow = visualSet.add(domain, arclen);
        visualRow.setLabel("arclen");
        visualRow.setStroke(PLOT_STROKE);
      }
      {
        Tensor div = phase.pmul(arclen.map(InvertUnlessZero.FUNCTION));
        Tensor domain = Range.of(0, div.length());
        Tensor values = div.multiply(RealScalar.of(-1));
        VisualRow visualRow = visualSet.add(domain, values);
        visualRow.setLabel("phase/arclen");
        visualRow.setStroke(PLOT_STROKE);
      }
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      jFreeChart.draw(graphics, new Rectangle2D.Double(0, 0, 800, 480));
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
