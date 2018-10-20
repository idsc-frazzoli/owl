// code by jph
package ch.ethz.idsc.owl.symlink;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.group.Se2CoveringGeodesic;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.owl.subdiv.curve.BSpline4CurveSubdivisions;
import ch.ethz.idsc.owl.subdiv.curve.BezierCurve;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.DeCasteljau;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenter;
import ch.ethz.idsc.owl.subdiv.demo.SpinnerLabel;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

class SymDemo {
  private static final Tensor ARROWHEAD_HI = Arrowhead.of(0.40);
  // private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.18);
  // ---
  private final TimerFrame timerFrame = new TimerFrame();
  // ---
  private Tensor control = Tensors.of(Array.zeros(3));
  private Tensor mouse = Array.zeros(3);
  private Integer min_index = null;
  protected Scalar MAGIC_C = RationalScalar.of(1, 2);

  SymDemo() {
    timerFrame.jFrame.setTitle(getClass().getSimpleName());
    SpinnerLabel<SmoothingKernel> spinnerKernel = new SpinnerLabel<>();
    SpinnerLabel<BSpline4CurveSubdivisions> spinnerBSpline4 = new SpinnerLabel<>();
    {
      JButton jButton = new JButton("clear");
      jButton.addActionListener(actionEvent -> control = Tensors.of(Array.zeros(3)));
      timerFrame.jToolBar.add(jButton);
    }
    JTextField jTextField = new JTextField(10);
    jTextField.setPreferredSize(new Dimension(100, 28));
    {
      timerFrame.jToolBar.add(jTextField);
    }
    {
      JButton jButton = new JButton("print");
      jButton.addActionListener(actionEvent -> {
        System.out.println(control);
        // long now = System.currentTimeMillis();
        File file = UserHome.file("" + jTextField.getText() + ".csv");
        // File file = new File("src/main/resources/subdiv/se2", now + ".csv");
        try {
          Export.of(file, control.map(CsvFormat.strict()));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      timerFrame.jToolBar.add(jButton);
    }
    JToggleButton jToggleBndy = new JToggleButton("bndy");
    jToggleBndy.setSelected(true);
    timerFrame.jToolBar.add(jToggleBndy);
    // ---
    JToggleButton jToggleLine = new JToggleButton("line");
    jToggleLine.setSelected(false);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    {
      JSlider jSlider = new JSlider(1, 999, 500);
      jSlider.setPreferredSize(new Dimension(500, 28));
      jSlider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
          MAGIC_C = RationalScalar.of(jSlider.getValue(), 1000);
          System.out.println(MAGIC_C);
        }
      });
      timerFrame.jToolBar.add(jSlider);
    }
    // ---
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        GraphicsUtil.setQualityHigh(graphics);
        mouse = geometricLayer.getMouseSe2State();
        if (Objects.nonNull(min_index))
          control.set(mouse, min_index);
        Tensor _control = control.copy();
        Tensor xya = null;
        final Tensor vector = Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::of));
        if (control.length() == 4) {
          // BezierCurve bezierCurve = new BezierCurve(Se2CoveringGeodesic.INSTANCE);
          DeCasteljau deCasteljau = new DeCasteljau(SymGeodesic.INSTANCE, vector);
          SymScalar symScalar = (SymScalar) deCasteljau.apply(MAGIC_C);
          // ScalarTensorFunction scalarTensorFunction = bezierCurve.evaluation(vector);
          // TensorUnaryOperator tensorUnaryOperator = BSpline4CurveSubdivision.split3(SymGeodesic.INSTANCE, RationalScalar.HALF)::cyclic;
          // Scalar tensor = (Scalar) scalarTensorFunction.apply(RationalScalar.of(1, 3));
          SymLinkBuilder symLinkBuilder = new SymLinkBuilder(control);
          SymLink symLink = symLinkBuilder.build(symScalar);
          new SymGeoRender(symLink).render(geometricLayer, graphics);
          xya = symLink.getPosition(Se2CoveringGeodesic.INSTANCE);
        } else //
        if (control.length() == 3 && false) {
          CurveSubdivision curveSubdivision = spinnerBSpline4.getValue().function.apply(SymGeodesic.INSTANCE);
          TensorUnaryOperator tensorUnaryOperator = curveSubdivision::cyclic;
          Tensor tensor = tensorUnaryOperator.apply(vector).Get(2);
          SymLinkBuilder symLinkBuilder = new SymLinkBuilder(control);
          SymLink symLink = symLinkBuilder.build((SymScalar) tensor);
          new SymGeoRender(symLink).render(geometricLayer, graphics);
          xya = symLink.getPosition(Se2CoveringGeodesic.INSTANCE);
        } else //
        if (control.length() % 2 == 1) {
          SmoothingKernel smoothingKernel = spinnerKernel.getValue();
          {
            int radius = (control.length() - 1) / 2;
            SymLinkImage symLinkImage = SymGenerate.window(smoothingKernel, radius);
            graphics.drawImage(symLinkImage.bufferedImage(), 0, 200, null);
          }
          // ---
          TensorUnaryOperator tensorUnaryOperator = //
              GeodesicCenter.of(SymGeodesic.INSTANCE, smoothingKernel);
          Tensor tensor = tensorUnaryOperator.apply(vector);
          SymLinkBuilder symLinkBuilder = new SymLinkBuilder(control);
          SymLink symLink = symLinkBuilder.build((SymScalar) tensor);
          new SymGeoRender(symLink).render(geometricLayer, graphics);
          xya = symLink.getPosition(Se2CoveringGeodesic.INSTANCE);
        }
        { // SE2
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
        }
        if (Objects.nonNull(xya)) {
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
          Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_HI);
          path2d.closePath();
          int rgb = 128 + 32;
          final Color color = new Color(rgb, rgb, rgb, 255);
          graphics.setColor(color);
          graphics.setStroke(new BasicStroke(1f));
          // graphics.setColor(color);
          graphics.fill(path2d);
          graphics.setColor(Color.BLACK);
          graphics.draw(path2d);
          geometricLayer.popMatrix();
        }
        if (jToggleLine.isSelected()) {
          BezierCurve bezierCurve = new BezierCurve(Se2CoveringGeodesic.INSTANCE);
          Tensor linear = bezierCurve.refine(_control, 1 << 8);
          graphics.setColor(new Color(0, 255, 0, 128));
          Path2D path2d = geometricLayer.toPath2D(linear);
          graphics.draw(path2d);
        }
        if (Objects.isNull(min_index)) {
          graphics.setColor(Color.GREEN);
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
          graphics.fill(geometricLayer.toPath2D(ARROWHEAD_HI));
          geometricLayer.popMatrix();
        }
      }
    });
    {
      spinnerKernel.setList(Arrays.asList(SmoothingKernel.values()));
      spinnerKernel.setValue(SmoothingKernel.GAUSSIAN);
      spinnerKernel.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "filter");
    }
    {
      spinnerBSpline4.setList(Arrays.asList(BSpline4CurveSubdivisions.values()));
      spinnerBSpline4.setValue(BSpline4CurveSubdivisions.DYN_SHARON);
      spinnerBSpline4.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "bspline4");
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
  }

  public static void main(String[] args) {
    SymDemo curveSubdivisionDemo = new SymDemo();
    curveSubdivisionDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    curveSubdivisionDemo.timerFrame.jFrame.setVisible(true);
  }
}
