// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.List;

import javax.swing.JButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.img.ImageCrop;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.sca.Floor;

/* package */ class UbongoDesigner extends AbstractDemo implements ActionListener {
  public static final Scalar FREE = UbongoBoard.FREE;
  private static final Tensor SQUARE = Tensors.fromString("{{0, 0}, {1, 0}, {1, 1}, {0, 1}}");
  // ---
  private final SpinnerLabel<Integer> spinnerUse = SpinnerLabel.of(2, 3, 4, 5, 6);
  private final JButton jButton = new JButton("solve");
  private final GridRender gridRender;
  private final Tensor template = Array.fill(() -> RealScalar.ZERO, 8, 10);

  public UbongoDesigner() {
    spinnerUse.setValue(4);
    spinnerUse.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), null);
    jButton.addActionListener(this);
    timerFrame.jToolBar.add(jButton);
    // ---
    Tensor matrix = Tensors.fromString("{{30, 0, 100}, {0, -30, 500}, {0, 0, 1}}");
    matrix = matrix.dot(Se2Matrix.of(Tensors.vector(0, 0, -Math.PI / 2)));
    timerFrame.geometricComponent.setModel2Pixel(matrix);
    timerFrame.geometricComponent.setOffset(100, 100);
    int row_max = template.length();
    int col_max = Unprotect.dimension1(template);
    gridRender = new GridRender(Subdivide.of(0, row_max, row_max), Subdivide.of(0, col_max, col_max));
    timerFrame.geometricComponent.jComponent.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == 1) {
          Tensor xya = timerFrame.geometricComponent.getMouseSe2CState().map(Floor.FUNCTION);
          int row = xya.Get(0).number().intValue();
          int col = xya.Get(1).number().intValue();
          if (0 <= row && row < row_max)
            if (0 <= col && col < col_max) {
              boolean free = template.get(row, col).equals(FREE);
              template.set(free ? RealScalar.ZERO : FREE, row, col);
            }
        }
      }
    });
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    graphics.setColor(Color.DARK_GRAY);
    int dimension1 = Unprotect.dimension1(template);
    for (int row = 0; row < template.length(); ++row) {
      for (int col = 0; col < dimension1; ++col) {
        Scalar scalar = template.Get(row, col);
        if (!scalar.equals(FREE)) {
          geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(row, col)));
          Path2D path2d = geometricLayer.toPath2D(SQUARE, true);
          graphics.fill(path2d);
          geometricLayer.popMatrix();
        }
      }
    }
    gridRender.render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    UbongoDesigner ubongoDesigner = new UbongoDesigner();
    // ubongoDesigner.timerFrame.configCoordinateOffset(100, 700);
    ubongoDesigner.setVisible(800, 600);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    TensorUnaryOperator res = ImageCrop.color(RealScalar.ZERO);
    Tensor result = res.apply(template);
    System.out.println(Pretty.of(result));
    UbongoBoard ubongoBoard = new UbongoBoard(result);
    List<List<UbongoEntry>> list = ubongoBoard.filter0(spinnerUse.getValue());
    if (list.isEmpty()) {
      System.err.println("no solutions");
    } else {
      UbongoBrowser ubongoBrowser = new UbongoBrowser(ubongoBoard, list);
      ubongoBrowser.setVisible(800, 600);
    }
  }
}
