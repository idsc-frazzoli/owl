// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;

/* package */ class KlotskiPlot {
  static final int RES = 128;
  static final double MARGIN = 0.08;
  static final Tensor BLOCKS = Tensors.of( //
      Tensors.vector(2, 2), //
      Tensors.vector(2, 1), //
      Tensors.vector(1, 2), //
      Tensors.vector(1, 1));
  static final Tensor HUARONG = Tensors.of( //
      Tensors.vector(0, 0), //
      Tensors.vector(7, 0), //
      Tensors.vector(7, 2), //
      Tensors.vector(6, 2), //
      Tensors.vector(6, 1), //
      Tensors.vector(1, 1), //
      Tensors.vector(1, 5), //
      Tensors.vector(6, 5), //
      Tensors.vector(6, 4), //
      Tensors.vector(7, 4), //
      Tensors.vector(7, 6), //
      Tensors.vector(0, 6));
  static final Tensor PENNANT = Tensors.of( //
      Tensors.vector(0, 0), //
      Tensors.vector(7, 0), //
      Tensors.vector(7, 1), //
      Tensors.vector(1, 1), //
      Tensors.vector(1, 5), //
      Tensors.vector(6, 5), //
      Tensors.vector(6, 3), //
      Tensors.vector(7, 3), //
      Tensors.vector(7, 6), //
      Tensors.vector(0, 6));
  static final Tensor TRAFFIC_JAM = Tensors.of( //
      Tensors.vector(0, 0), //
      Tensors.vector(7, 0), //
      Tensors.vector(7, 8), //
      Tensors.vector(6, 8), //
      Tensors.vector(6, 1), //
      Tensors.vector(1, 1), //
      Tensors.vector(1, 7), //
      Tensors.vector(4, 7), //
      Tensors.vector(4, 8), //
      Tensors.vector(0, 8));
  // ---
  private final Tensor border;
  private final int sx;
  private final int sy;

  public KlotskiPlot(KlotskiProblem klotskiProblem, Tensor border) {
    Tensor size = klotskiProblem.size();
    sx = size.Get(0).number().intValue();
    sy = size.Get(1).number().intValue();
    this.border = border;
  }

  BufferedImage plot(Tensor board) {
    BufferedImage bufferedImage = new BufferedImage(sy * RES, sx * RES, BufferedImage.TYPE_INT_ARGB);
    GeometricLayer geometricLayer = GeometricLayer.of(Tensors.matrix(new Number[][] { //
        { 0, RES, 0 }, //
        { RES, 0, 0 }, //
        { 0, 0, 1 } }));
    Graphics2D graphics = bufferedImage.createGraphics();
    new Plot(board).render(geometricLayer, graphics);
    return bufferedImage;
  }

  class Plot implements RenderInterface {
    private final Tensor board;

    Plot(Tensor board) {
      this.board = Objects.requireNonNull(board);
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, sy * RES, sx * RES);
      graphics.setColor(new Color(128, 128, 255));
      graphics.fill(geometricLayer.toPath2D(border));
      for (Tensor stone : board) {
        int index = stone.Get(0).number().intValue();
        geometricLayer.pushMatrix(Se2Matrix.translation(stone.extract(1, 3)));
        graphics.setColor(index == 0 ? new Color(255, 128 - 32, 128 - 32) : new Color(255, 128, 128));
        {
          Tensor polygon = Tensors.empty();
          if (index < 4) {
            Tensor format = BLOCKS.get(index);
            polygon = Tensors.of( //
                format.pmul(Tensors.vector(0, 0)).add(Tensors.vector(+MARGIN, +MARGIN)), //
                format.pmul(Tensors.vector(0, 1)).add(Tensors.vector(+MARGIN, -MARGIN)), //
                format.pmul(Tensors.vector(1, 1)).add(Tensors.vector(-MARGIN, -MARGIN)), //
                format.pmul(Tensors.vector(1, 0)).add(Tensors.vector(-MARGIN, +MARGIN)));
          } else {
            switch (index) {
            case 4:
              polygon = Tensors.of( //
                  Tensors.vector(0 + MARGIN, 0 + MARGIN), //
                  Tensors.vector(2 - MARGIN, 0 + MARGIN), //
                  Tensors.vector(2 - MARGIN, 1 - MARGIN), //
                  Tensors.vector(1 - MARGIN, 1 - MARGIN), //
                  Tensors.vector(1 - MARGIN, 2 - MARGIN), //
                  Tensors.vector(0 + MARGIN, 2 - MARGIN));
              break;
            case 5:
              polygon = Tensors.of( //
                  Tensors.vector(2 - MARGIN, 2 - MARGIN), //
                  Tensors.vector(0 + MARGIN, 2 - MARGIN), //
                  Tensors.vector(0 + MARGIN, 1 + MARGIN), //
                  Tensors.vector(1 + MARGIN, 1 + MARGIN), //
                  Tensors.vector(1 + MARGIN, 0 + MARGIN), //
                  Tensors.vector(2 - MARGIN, 0 + MARGIN));
              break;
            default:
              throw new RuntimeException("index=" + index);
            }
          }
          graphics.fill(geometricLayer.toPath2D(polygon));
        }
        geometricLayer.popMatrix();
      }
    }
  }

  public static void main(String[] args) throws ClassNotFoundException, IOException, DataFormatException {
    KlotskiProblem klotskiProblem = TrafficJam.INSTANCE;
    List<StateTime> list = Import.object(HomeDirectory.file(klotskiProblem.name() + ".object"));
    System.out.println(list.size());
    int index = 0;
    File folder = HomeDirectory.Pictures(klotskiProblem.name());
    folder.mkdir();
    KlotskiPlot klotskiPlot = new KlotskiPlot(klotskiProblem, TRAFFIC_JAM);
    for (StateTime stateTime : list) {
      BufferedImage bufferedImage = klotskiPlot.plot(stateTime.state());
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.DARK_GRAY);
      graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, RES / 2));
      graphics.drawString("move " + index, RES / 8, RES / 2);
      ImageIO.write(bufferedImage, "png", new File(folder, String.format("%03d.png", index)));
      ++index;
    }
  }
}
