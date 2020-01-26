// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;

/* package */ enum HuarongPlot {
  ;
  static final int RES = 128;
  static final Tensor FORMAT = Tensors.of( //
      Tensors.vector(2, 2), //
      Tensors.vector(2, 1), //
      Tensors.vector(1, 2), //
      Tensors.vector(1, 1));
  static final Tensor FRAME = Tensors.of( //
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
      Tensors.vector(0, 6) //
  );

  static BufferedImage plot(Tensor board) {
    BufferedImage bufferedImage = new BufferedImage(6 * RES, 7 * RES, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 6 * RES, 7 * RES);
    GeometricLayer geometricLayer = GeometricLayer.of(Tensors.matrix(new Number[][] { //
        { 0, RES, 0 }, //
        { RES, 0, 0 }, //
        { 0, 0, 1 } }));
    graphics.setColor(new Color(128, 128, 255));
    graphics.fill(geometricLayer.toPath2D(FRAME));
    double margin = 0.08;
    for (Tensor stone : board) {
      int index = stone.Get(0).number().intValue();
      Tensor format = FORMAT.get(index);
      geometricLayer.pushMatrix(Se2Matrix.translation(stone.extract(1, 3)));
      Tensor polygon = Tensors.of( //
          format.pmul(Tensors.vector(0, 0)).add(Tensors.vector(+margin, +margin)), //
          format.pmul(Tensors.vector(0, 1)).add(Tensors.vector(+margin, -margin)), //
          format.pmul(Tensors.vector(1, 1)).add(Tensors.vector(-margin, -margin)), //
          format.pmul(Tensors.vector(1, 0)).add(Tensors.vector(-margin, +margin)));
      graphics.setColor(index == 0 ? new Color(255, 128 - 32, 128 - 32) : new Color(255, 128, 128));
      graphics.fill(geometricLayer.toPath2D(polygon));
      geometricLayer.popMatrix();
    }
    // geometricLayer.
    return bufferedImage;
  }

  public static void main(String[] args) throws ClassNotFoundException, IOException, DataFormatException {
    KlotskiProblem huarong = Pennant.PUZZLE;
    List<StateTime> list = Import.object(HomeDirectory.file(huarong.name() + ".object"));
    System.out.println(list.size());
    int index = 0;
    File folder = HomeDirectory.Pictures(huarong.name());
    folder.mkdir();
    for (StateTime stateTime : list) {
      BufferedImage bufferedImage = plot(stateTime.state());
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.DARK_GRAY);
      graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, RES / 2));
      graphics.drawString("move " + index, RES / 8, RES / 2);
      ImageIO.write(bufferedImage, "png", new File(folder, String.format("%03d.png", index)));
      ++index;
    }
  }
}
