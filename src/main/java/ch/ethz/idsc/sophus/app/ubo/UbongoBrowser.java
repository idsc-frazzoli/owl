// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.img.ImageRotate;
import ch.ethz.idsc.tensor.io.ImageFormat;

/* package */ class UbongoBrowser extends AbstractDemo {
  private final UbongoBoard ubongoBoard;
  private final List<List<UbongoEntry>> list;
  private final SpinnerLabel<Integer> spinnerIndex = new SpinnerLabel<>();

  public UbongoBrowser(UbongoBoard ubongoBoard, List<List<UbongoEntry>> list) {
    this.ubongoBoard = ubongoBoard;
    this.list = list;
    spinnerIndex.setList(IntStream.range(0, list.size()).boxed().collect(Collectors.toList()));
    spinnerIndex.setIndex(0);
    spinnerIndex.addToComponentReduced(timerFrame.jToolBar, new Dimension(40, 28), "index");
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    List<UbongoEntry> solution = list.get(spinnerIndex.getIndex());
    {
      List<Integer> size = Dimensions.of(ubongoBoard.mask);
      Tensor tensor = UbongoRender.of(size, solution);
      graphics.drawImage(ImageFormat.of(tensor), 100, 100, size.get(1) * 20, size.get(0) * 20, null);
    }
    int pix = 0;
    for (UbongoEntry ubongoEntry : solution) {
      UbongoEntry ubongoPiece = new UbongoEntry();
      ubongoPiece.stamp = ImageRotate.cw(ubongoEntry.ubongo.mask());
      ubongoPiece.ubongo = ubongoEntry.ubongo;
      List<Integer> size = Dimensions.of(ubongoPiece.stamp);
      Tensor tensor = UbongoRender.of(size, Arrays.asList(ubongoPiece));
      // List<Integer> size2 = Dimensions.of(tensor);
      int piw = size.get(1) * 10;
      graphics.drawImage(ImageFormat.of(tensor), 100 + pix, 300, piw, size.get(0) * 10, null);
      pix += piw + 20;
    }
  }

  public static void main(String[] args) {
    UbongoBoards ubongoBoards = UbongoBoards.MICKEY_2;
    List<List<UbongoEntry>> list = ubongoBoards.solve();
    if (!list.isEmpty()) {
      UbongoBrowser ubongoBrowser = new UbongoBrowser(ubongoBoards.board(), list);
      ubongoBrowser.setVisible(800, 600);
    }
  }
}
