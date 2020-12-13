// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.img.ImageRotate;

/* package */ class UbongoViewer extends AbstractDemo {
  private static final int MARGIN_X = 320;
  private static final int MARGIN_Y = 13;
  // 61.1465
  private static final int SCALE = 46;
  private static final int ZCALE = 10;
  private static final int MAX_X = 10;
  private static final int MAX_Y = 8;

  public static int maxWidth() {
    return MARGIN_X + MAX_X * SCALE + 1;
  }

  public static int maxHeight() {
    return MAX_Y * SCALE + MARGIN_Y * 2;
  }

  // ---
  private final SpinnerLabel<UbongoPublish> spinnerIndex = SpinnerLabel.of(UbongoPublish.values());

  public UbongoViewer() {
    spinnerIndex.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "index");
    spinnerIndex.setValue(UbongoPublish.CHRISTMT);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    draw(graphics, spinnerIndex.getValue(), SCALE);
  }

  public static void draw(Graphics2D graphics, UbongoPublish ubongoPublish, int SCALE) {
    UbongoBoard ubongoBoard = ubongoPublish.ubongoBoards.board();
    List<List<UbongoEntry>> solutions = UbongoLoader.INSTANCE.load(ubongoPublish.ubongoBoards);
    {
      Tensor mask = ubongoBoard.mask;
      int scale = SCALE;
      graphics.setColor(Color.GRAY);
      List<Integer> size = Dimensions.of(mask);
      for (int row = 0; row < size.get(0); ++row)
        for (int col = 0; col < size.get(1); ++col) {
          Scalar scalar = mask.Get(row, col);
          if (Scalars.nonZero(scalar))
            graphics.fillRect(MARGIN_X + col * scale, MARGIN_Y + row * scale, scale - 1, scale - 1);
        }
    }
    int piy = MARGIN_Y;
    int count = 0;
    graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
    for (int index : ubongoPublish.list) {
      ++count;
      graphics.setColor(Color.DARK_GRAY);
      int pix = 50;
      graphics.drawString("" + count, 2, piy + 20);
      List<UbongoEntry> solution = solutions.get(index);
      for (UbongoEntry ubongoEntry : solution) {
        UbongoEntry ubongoPiece = new UbongoEntry();
        ubongoPiece.stamp = ImageRotate.cw(ubongoEntry.ubongo.mask());
        ubongoPiece.ubongo = ubongoEntry.ubongo;
        List<Integer> size = Dimensions.of(ubongoPiece.stamp);
        int piw = size.get(1) * ZCALE;
        int scale = ZCALE;
        Tensor mask = ubongoPiece.stamp;
        graphics.setColor(Color.GRAY);
        for (int row = 0; row < size.get(0); ++row)
          for (int col = 0; col < size.get(1); ++col) {
            Scalar scalar = mask.Get(row, col);
            if (Scalars.nonZero(scalar))
              graphics.fillRect(pix + col * scale, piy + row * scale, scale, scale);
          }
        pix += piw + 2 * ZCALE;
      }
      piy += 4 * ZCALE + 2 * ZCALE;
    }
  }

  public static void main(String[] args) {
    UbongoViewer ubongoViewer = new UbongoViewer();
    ubongoViewer.setVisible(1200, 600);
  }
}
