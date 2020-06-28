// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.krg.Biinvariant;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ abstract class ExportCoordinateDemo extends ScatteredSetCoordinateDemo implements ActionListener {
  public static List<Biinvariant> distinct() {
    return Arrays.asList( //
        Biinvariant.METRIC, //
        Biinvariant.GARDEN, //
        Biinvariant.ANCHOR, //
        Biinvariant.HARBOR);
  }

  private static final int REFINEMENT = 120;
  private final JButton jButtonExport = new JButton("export");

  public ExportCoordinateDemo( //
      boolean addRemoveControlPoints, //
      List<GeodesicDisplay> list, //
      List<LogWeighting> array) {
    super(addRemoveControlPoints, list, array);
    {
      jButtonExport.addActionListener(this);
      timerFrame.jToolBar.add(jButtonExport);
    }
  }

  @Override
  public final void actionPerformed(ActionEvent actionEvent) {
    File root = HomeDirectory.Pictures(getClass().getSimpleName(), geodesicDisplay().toString());
    root.mkdirs();
    for (Biinvariant biinvariant : distinct()) {
      LogWeighting logWeighting = logWeighting();
      Tensor sequence = getGeodesicControlPoints();
      TensorUnaryOperator tensorUnaryOperator = logWeighting.from( //
          biinvariant, //
          geodesicDisplay().vectorLogManifold(), //
          variogram(), //
          sequence);
      System.out.print("computing " + biinvariant);
      Tensor wgs = compute(tensorUnaryOperator, REFINEMENT);
      List<Integer> dims = Dimensions.of(wgs);
      Tensor _wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
      ArrayPlotRender arrayPlotRender = ArrayPlotRender.rescale(_wgp, colorDataGradient(), 1);
      BufferedImage bufferedImage = arrayPlotRender.export();
      try {
        ImageIO.write(bufferedImage, "png", new File(root, biinvariant.toString() + ".png"));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      System.out.println(" done");
    }
    System.out.println("all done");
  }

  abstract Tensor compute(TensorUnaryOperator weightingInterface, int refinement);
}
