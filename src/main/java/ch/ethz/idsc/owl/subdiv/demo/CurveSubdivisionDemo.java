// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.CurvatureComb;
import ch.ethz.idsc.owl.math.planar.ExtractXY;
import ch.ethz.idsc.owl.subdiv.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicInterface;
import ch.ethz.idsc.owl.subdiv.curve.RnGeodesic;
import ch.ethz.idsc.owl.subdiv.curve.Se2CoveringGeodesic;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;

class CurveSubdivisionDemo {
  private static final Tensor ARROWHEAD_HI = Tensors.matrixDouble( //
      new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).multiply(RealScalar.of(1.2));
  private static final Tensor ARROWHEAD_LO = Tensors.matrixDouble( //
      new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).multiply(RealScalar.of(.6));
  private static final Tensor CIRCLE_HI = CirclePoints.of(15).multiply(RealScalar.of(.1));
  private static final Scalar COMB_SCALE = DoubleScalar.of(.5);
  private static final Color COLOR_CURVATURE_COMB = new Color(0, 0, 0, 128);
  // private static final Tensor DUBILAB = //
  // ResourceData.of("/dubilab/controlpoints/eight/20180603.csv").multiply(RealScalar.of(.4)).unmodifiable();
  // ---
  private Tensor control = Tensors.of(Array.zeros(3));
  private final TimerFrame timerFrame = new TimerFrame();
  private Tensor mouse = Array.zeros(3);
  private Integer min_index = null;
  // private static final Tensor FCURVE = LogarithmicSpiralCurve.of(RealScalar.of(2), RealScalar.of(0.1759));
  // Nest.of( //
  // new FourPointCurveSubdivision(RnGeodesic.INSTANCE)::cyclic, //
  // DUBILAB, 5);
  // FresnelCurve.of(300).multiply(RealScalar.of(10));
  private boolean printref = false;
  private boolean ref2ctrl = false;

  CurveSubdivisionDemo() {
    // BufferedImage image = ImageIO.read(UserHome.file("trebleclef25.png"));
    SpinnerLabel<CurveSubdivisionSchemes> spinnerLabel = new SpinnerLabel<>();
    // SpinnerLabel<CurveSubdivisionSchemes> spinnerAlpha = new SpinnerLabel<>();
    SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
    // control = Tensors.fromString(
    // "{{499/60, -43/60, 4.1887902047863905}, {-19/60, 11/20, 2.617993877991494}, {41/15, 43/10, 1.0471975511965976}, {-221/60, 16/5, 3.141592653589793}, {-9,
    // 247/60, 0.7853981633974483}, {-46/15, 43/15, 3.141592653589793}, {-451/60, 1, 0.7853981633974483}, {431/60, 9/5, 4.1887902047863905}}");
    // control = Tensors.fromString( //
    // "{{31/15, -1/3, 1.0471975511965976}, {-27/10, 107/60, 3.665191429188092}, {23/6, -44/15, 6.8067840827778845}, {-47/15, 409/60, 9.686577348568528}}");
    // TREBLE CLEF
    // {{-23/30, 17/5, 0.0}, {-8/15, -73/60, -1.0471975511965976}, {-35/12, -7/2, 0.2617993877991494}, {-1/5, -133/30, 2.356194490192345}, {-33/20, -61/15,
    // 6.8067840827778845}, {-17/30, -131/30, 7.853981633974482}, {-31/15, -173/60, 5.497787143782138}, {1/5, -91/20, 1.832595714594046}, {-3, -26/15,
    // -1.0471975511965976}, {-37/30, 7/20, -1.308996938995747}, {-7/20, 5/2, -0.5235987755982988}, {-79/60, 47/30, 1.0471975511965976}, {-17/15, -11/30,
    // 1.5707963267948966}, {-3/20, -127/20, 1.5707963267948966}, {-1/12, -421/60, 1.0471975511965976}, {-37/15, -27/4, -1.308996938995747}, {-8/5, -29/4,
    // -6.021385919380436}}
    control = Tensors.fromString( //
        "{{0, 0, 0}, {4, 0, 0.0}, {8, 0, 0.0}, {8, -3, -3.141592653589793}, {4, -3, -3.141592653589793}, {0, -3, -3.141592653589793}," //
            + "{0, 3, -6.283185307179586}," //
            + "{4, 3, -6.283185307179586}, {8, 3, -6.283185307179586}}");
    control = Tensors.fromString("{{-8,0,0},{-4,0,0},{0,0,0}}");
    // Math.PI;
    control = DubinsGenerator.of(Tensors.vector(0, 0, -1), Tensors.fromString("{{2,0,0},{2,0,1.3},{3,0,0},{4,0,-4.2},{5,0,0},{5,0,3.14159265},{3,0,-3}}"));
    control = DubinsGenerator.of(Tensors.vector(0, 0, -Math.PI - 1), //
        Tensors.fromString("{{2,0,0},{2,0,2.5708},{1,0,2.1},{1.5,0,0},{2.3,0,-1.2},{1.5,0,0}}"));
    control = Tensors.fromString(
        "{{11/15, 1/2, -1.0471975511}, {149/60, 43/30, 1.04719755}, {-19/20, 19/30, 2.0943951}, {-44/15, 3/4, 0.2617993}, {-71/60, 17/12, -2.0943951}}");
    control = DubinsGenerator.of(Tensors.vector(0, 0, Math.PI / 2), //
        Tensors.fromString("{{1.5,0,0},{1.5,0,0},{2,0,3.141592653589793},{1.5,0,0},{1.5,0,0},{4,0,3.141592653589793},{1.5,0,0},{1.5,0,0}}"));
    control = DubinsGenerator.of(Tensors.vector(0, 0, 0.5), //
        Tensors.fromString("{{1,0,0},{1,0,0},{2,0,2.5708},{1,0,2.1},{1.5,0,0},{2.3,0,-1.2},{1.5,0,0},{4,0,3.14159},{2,0,3.14159},{2,0,0}}"));
    control = Tensors.fromString( //
        "{{-23/30, 17/5, 0.0}, {-8/15, -73/60, -1.0471975511965976}, {-35/12, -7/2, 0.2617993877991494}, {-1/5, -133/30, 2.356194490192345}, {-103/60, -39/10, 6.544984694978735}, {-17/30, -131/30, 7.853981633974482}, {-31/15, -173/60, 5.497787143782138}, {1/5, -91/20, 1.832595714594046}, {-3, -26/15, -1.0471975511965976}, {-37/30, 7/20, -1.308996938995747}, {-7/20, 5/2, -0.5235987755982988}, {-13/10, 23/15, 1.0471975511965976}, {-59/60, -47/30, 1.5707963267948966}}");
    control = DubinsGenerator.of(Tensors.vector(0, 0, 0), //
        Tensors.fromString("{{1,0,0},{1,0,0},{4,-1,3.1415/2},{2,0,3.14159},{1,0,3.14159}}"));
    control = DubinsGenerator.of(Tensors.vector(0, 0, Math.PI / 4 - .15), //
        Tensors.fromString("{{1.5,0,0},{1.5,0,0},{9,0,7.85398},{3,0,0},{1.5,0,0}}"));
    control = DubinsGenerator.of(Tensors.vector(0, 0, 0), Tensors.fromString( //
        "{{3,0,3.141592653589793},{2.5,0,-3.141592653589793},{2,0,3.141592653589793},{1.5,0,-3.141592653589793},{1.0,0,3.141592653589793}}"));
    // dubins intro
    control = DubinsGenerator.of(Tensors.vector(0, 0, 3.1 + 1), Tensors.fromString( //
        "{{2,0,0},{3.5,0,-4.5},{3.5,0,0},{1.6,0,3},{2.3,0,2}}"));
    // pathological
    control = Tensors.fromString("{{0, 0, 0}, {2, 0, 1.308996938995747}, {4, 0, 0.5235987755982988}}");
    {
      JButton jButton = new JButton("clear");
      jButton.addActionListener(actionEvent -> control = Tensors.of(Array.zeros(3)));
      timerFrame.jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton("print");
      jButton.addActionListener(actionEvent -> {
        System.out.println(control);
        // long now = System.currentTimeMillis();
        // File file = new File("src/main/resources/subdiv/se2", now + ".csv");
        try {
          // Export.of(file, control.map(CsvFormat.strict()));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      timerFrame.jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton("p-ref");
      jButton.addActionListener(actionEvent -> printref = true);
      timerFrame.jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton("r2c");
      jButton.addActionListener(actionEvent -> ref2ctrl = true);
      timerFrame.jToolBar.add(jButton);
    }
    JToggleButton jToggleCtrl = new JToggleButton("ctrl");
    jToggleCtrl.setSelected(true);
    timerFrame.jToolBar.add(jToggleCtrl);
    // ---
    JToggleButton jToggleBndy = new JToggleButton("bndy");
    jToggleBndy.setSelected(true);
    timerFrame.jToolBar.add(jToggleBndy);
    // ---
    JToggleButton jToggleComb = new JToggleButton("comb");
    jToggleComb.setSelected(true);
    timerFrame.jToolBar.add(jToggleComb);
    // ---
    JToggleButton jToggleLine = new JToggleButton("line");
    jToggleLine.setSelected(false);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    JToggleButton jToggleCyclic = new JToggleButton("cyclic");
    // jToggleCyclic.setSelected(true);
    timerFrame.jToolBar.add(jToggleCyclic);
    // ---
    JToggleButton jToggleButton = new JToggleButton("R2");
    jToggleButton.setSelected(Dimensions.of(control).get(1) == 2);
    timerFrame.jToolBar.add(jToggleButton);
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        // graphics.drawImage(image, 100, 100, null);
        GraphicsUtil.setQualityHigh(graphics);
        mouse = geometricLayer.getMouseSe2State();
        if (Objects.nonNull(min_index))
          control.set(mouse, min_index);
        {
          // graphics.setColor(new Color(128 - 64, 255, 128, 255));
          // graphics.draw(geometricLayer.toPath2D(FCURVE));
        }
        CurveSubdivisionSchemes scheme = spinnerLabel.getValue();
        Function<GeodesicInterface, CurveSubdivision> function = spinnerLabel.getValue().function;
        boolean isR2 = jToggleButton.isSelected();
        boolean isCyclic = jToggleCyclic.isSelected();
        Tensor _control = control.copy();
        if (jToggleBndy.isSelected() && !isCyclic && 1 < _control.length()) {
          switch (scheme) {
          case BSPLINE2:
          case BSPLINE4:
            _control = Join.of( //
                _control.extract(0, 1), //
                _control, //
                _control.extract(_control.length() - 1, _control.length()));
            break;
          default:
            break;
          }
        }
        int levels = spinnerRefine.getValue();
        final Tensor refined;
        if (isR2) {
          CurveSubdivision curveSubdivision = function.apply(RnGeodesic.INSTANCE);
          Tensor rnctrl = Tensor.of(_control.stream().map(ExtractXY::of));
          TensorUnaryOperator tuo = jToggleCyclic.isSelected() //
              ? curveSubdivision::cyclic
              : curveSubdivision::string;
          refined = Nest.of(tuo, rnctrl, levels);
          {
            graphics.setColor(new Color(0, 0, 255, 128));
            graphics.draw(geometricLayer.toPath2D(refined));
          }
          graphics.setColor(new Color(255, 128, 128, 255));
          for (Tensor point : _control) {
            geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point.copy().append(RealScalar.ZERO)));
            Path2D path2d = geometricLayer.toPath2D(CIRCLE_HI);
            path2d.closePath();
            graphics.setColor(new Color(255, 128, 128, 64));
            graphics.fill(path2d);
            graphics.setColor(new Color(255, 128, 128, 255));
            graphics.draw(path2d);
            geometricLayer.popMatrix();
          }
        } else { // SE2
          if (jToggleCtrl.isSelected())
            for (Tensor point : control) {
              geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
              Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_HI);
              path2d.closePath();
              graphics.setColor(new Color(255, 128, 128, 64));
              graphics.fill(path2d);
              graphics.setColor(new Color(255, 128, 128, 255));
              graphics.draw(path2d);
              geometricLayer.popMatrix();
            }
          CurveSubdivision curveSubdivision = function.apply(Se2CoveringGeodesic.INSTANCE);
          // Function<GeodesicInterface, CurveSubdivision> fun = spinnerAlpha.getValue().function;
          // CurveSubdivision angleSubdivision = fun.apply(Se2CoveringGeodesic.INSTANCE);
          TensorUnaryOperator tuo = isCyclic //
              ? curveSubdivision::cyclic
              : curveSubdivision::string;
          // TensorUnaryOperator tuo = new SplitScheme(curveSubdivision, angleSubdivision);
          refined = Nest.of(tuo, _control, levels);
        }
        if (jToggleLine.isSelected()) {
          CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(Se2CoveringGeodesic.INSTANCE);
          TensorUnaryOperator tuo = isCyclic //
              ? curveSubdivision::cyclic
              : curveSubdivision::string;
          Tensor linear = Nest.of(tuo, _control, 8);
          graphics.setColor(new Color(0, 255, 0, 128));
          Path2D path2d = geometricLayer.toPath2D(linear);
          if (isCyclic)
            path2d.closePath();
          // graphics.setStroke(new BasicStroke(1.25f));
          graphics.draw(path2d);
          // graphics.setStroke(new BasicStroke(1f));
        }
        {
          graphics.setColor(Color.BLUE);
          Path2D path2d = geometricLayer.toPath2D(refined);
          if (isCyclic)
            path2d.closePath();
          graphics.setStroke(new BasicStroke(1.25f));
          graphics.draw(path2d);
          graphics.setStroke(new BasicStroke(1f));
        }
        if (jToggleComb.isSelected()) {
          graphics.setColor(COLOR_CURVATURE_COMB);
          Path2D path2d = geometricLayer.toPath2D(CurvatureComb.of(refined, COMB_SCALE, isCyclic));
          if (isCyclic)
            path2d.closePath();
          graphics.draw(path2d);
        }
        if (!isR2) {
          if (levels < 5) {
            for (Tensor point : refined) {
              geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
              Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_LO);
              geometricLayer.popMatrix();
              int rgb = 128 + 32;
              path2d.closePath();
              graphics.setColor(new Color(rgb, rgb, rgb, 128 + 64));
              graphics.fill(path2d);
              graphics.setColor(Color.BLACK);
              graphics.draw(path2d);
            }
          }
        }
        if (Objects.isNull(min_index)) {
          graphics.setColor(Color.GREEN);
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
          graphics.fill(geometricLayer.toPath2D(isR2 ? CIRCLE_HI : ARROWHEAD_HI));
          geometricLayer.popMatrix();
        }
        if (printref) {
          printref = false;
          System.out.println(refined);
        }
        if (ref2ctrl) {
          ref2ctrl = false;
          control = refined;
        }
      }
    });
    {
      spinnerLabel.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
      spinnerLabel.setArray(CurveSubdivisionSchemes.values());
      spinnerLabel.setIndex(2);
      spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(150, 28), "scheme");
    }
    // {
    // spinnerAlpha.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
    // spinnerAlpha.setArray(CurveSubdivisionSchemes.values());
    // spinnerAlpha.setIndex(2);
    // spinnerAlpha.addToComponentReduced(timerFrame.jToolBar, new Dimension(150, 28), "scheme");
    // }
    {
      spinnerRefine.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
      spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerRefine.setValue(9);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
    timerFrame.geometricComponent.jComponent.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == 1) {
          if (Objects.isNull(min_index)) {
            Scalar cmp = DoubleScalar.of(.2);
            int index = 0;
            for (Tensor point : control) {
              Scalar distance = Norm._2.between(point.extract(0, 2), mouse.extract(0, 2));
              if (Scalars.lessThan(distance, cmp)) {
                cmp = distance;
                min_index = index;
              }
              ++index;
            }
            if (min_index == null) {
              min_index = control.length();
              control.append(mouse);
            }
          } else {
            min_index = null;
          }
        }
      }
    });
    // timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
  }

  public static void main(String[] args) {
    CurveSubdivisionDemo curveSubdivisionDemo = new CurveSubdivisionDemo();
    curveSubdivisionDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    curveSubdivisionDemo.timerFrame.jFrame.setVisible(true);
  }
}
