// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JTextField;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.ExtractXY;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.ConvexHull;
import ch.ethz.idsc.tensor.opt.HungarianAlgorithm;
import ch.ethz.idsc.tensor.opt.SpatialMedian;
import ch.ethz.idsc.tensor.opt.SphereFit;
import ch.ethz.idsc.tensor.red.Norm;

class SphereFitDemo {
  private static final Tensor CIRCLE_HI = CirclePoints.of(15).multiply(RealScalar.of(.1));
  // ---
  private Tensor control = Tensors.of(Array.zeros(3));
  private final TimerFrame timerFrame = new TimerFrame();
  private Tensor mouse = Array.zeros(3);
  private Integer min_index = null;

  SphereFitDemo() {
    SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
    {
      Tensor blub = Tensors.fromString("{{1,0,0},{1,0,0},{2,0,2.5708},{1,0,2.1},{1.5,0,0},{2.3,0,-1.2},{1.5,0,0},{4,0,3.14159},{2,0,3.14159},{2,0,0}}");
      control = DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(blub.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1)))));
    }
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
    // ---
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        GraphicsUtil.setQualityHigh(graphics);
        mouse = geometricLayer.getMouseSe2State();
        if (Objects.nonNull(min_index))
          control.set(mouse, min_index);
        Tensor rnctrl = Tensor.of(control.stream().map(ExtractXY::of));
        Optional<Tensor> some = SphereFit.of(rnctrl);
        if (some.isPresent()) {
          Tensor center = some.get().get(0);
          Scalar radius = some.get().Get(1);
          {
            geometricLayer.pushMatrix(Se2Utils.toSE2Translation(center));
            graphics.setColor(new Color(0, 0, 255, 128));
            Path2D path2d = geometricLayer.toPath2D(CirclePoints.of(40).multiply(radius));
            path2d.closePath();
            graphics.draw(path2d);
            geometricLayer.popMatrix();
          }
        }
        {
          Tensor hull = ConvexHull.of(rnctrl);
          Path2D path2d = geometricLayer.toPath2D(hull);
          path2d.closePath();
          graphics.setColor(new Color(128, 255, 128, 255));
          graphics.draw(path2d);
        }
        {
          Tensor circle = CirclePoints.of(10).multiply(RealScalar.of(3));
          {
            graphics.setColor(Color.GRAY);
            Path2D path2d = geometricLayer.toPath2D(circle);
            path2d.closePath();
            graphics.draw(path2d);
          }
          Tensor matrix = Tensors.matrix((i, j) -> //
          Norm._2.between(rnctrl.get(i), circle.get(j)), rnctrl.length(), circle.length());
          HungarianAlgorithm hungarianAlgorithm = HungarianAlgorithm.of(matrix);
          int[] matching = hungarianAlgorithm.matching();
          graphics.setColor(Color.RED);
          for (int index = 0; index < matching.length; ++index)
            if (matching[index] != HungarianAlgorithm.UNASSIGNED) {
              Path2D path2d = geometricLayer.toPath2D(Tensors.of(rnctrl.get(index), circle.get(matching[index])));
              graphics.draw(path2d);
            }
        }
        {
          Tensor weiszfeld = SpatialMedian.with(1e-4).uniform(rnctrl).get();
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(weiszfeld.copy().append(RealScalar.ZERO)));
          Path2D path2d = geometricLayer.toPath2D(CIRCLE_HI);
          path2d.closePath();
          graphics.setColor(new Color(128, 128, 255, 64));
          graphics.fill(path2d);
          graphics.setColor(new Color(128, 128, 255, 255));
          graphics.draw(path2d);
          geometricLayer.popMatrix();
        }
        {
          graphics.setColor(new Color(255, 128, 128, 255));
          for (Tensor point : control) {
            geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point.copy().append(RealScalar.ZERO)));
            Path2D path2d = geometricLayer.toPath2D(CIRCLE_HI);
            path2d.closePath();
            graphics.setColor(new Color(255, 128, 128, 64));
            graphics.fill(path2d);
            graphics.setColor(new Color(255, 128, 128, 255));
            graphics.draw(path2d);
            geometricLayer.popMatrix();
          }
        }
        if (Objects.isNull(min_index)) {
          graphics.setColor(Color.GREEN);
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
          graphics.fill(geometricLayer.toPath2D(CIRCLE_HI));
          geometricLayer.popMatrix();
        }
      }
    });
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
  }

  public static void main(String[] args) {
    SphereFitDemo sphereFitDemo = new SphereFitDemo();
    sphereFitDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    sphereFitDemo.timerFrame.jFrame.setVisible(true);
  }
}
