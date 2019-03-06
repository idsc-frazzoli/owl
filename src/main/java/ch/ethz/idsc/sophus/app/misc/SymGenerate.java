// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.sophus.sym.SymLinkImage;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.sophus.sym.SymScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum SymGenerate {
  ;
  public static void custom() throws IOException {
    Scalar s0 = SymScalar.leaf(0);
    Scalar s1 = SymScalar.leaf(1);
    Scalar s2 = SymScalar.leaf(2);
    Scalar s3 = SymScalar.of(s0, s1, RealScalar.of(2));
    Scalar s4 = SymScalar.of(s3, s2, RationalScalar.of(1, 3));
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) s4);
    ImageIO.write(symLinkImage.bufferedImageCropped(true), "png", HomeDirectory.Pictures("export/custom.png"));
  }

  public static void main(String[] args) throws IOException {
    {
      int degree = 5;
      int upper = 9;
      Scalar parameter = RationalScalar.of(2 * 3 + 2, 3);
      SymLinkImage symLinkImage = SymLinkImages.bspline(degree, upper + 1, parameter);
      ImageIO.write(symLinkImage.bufferedImage(), "png", HomeDirectory.Pictures("deboor5.png"));
    }
    // BufferedImage bufferedImage =
    // SymLinkImages.smoothingKernel(SmoothingKernel.GAUSSIAN, 5);
    // ImageIO.write(bufferedImage, "png", UserHome.Pictures("export/" + wf.name().toLowerCase() + radius + ".png"));
    // for (WindowFunctions windowFunctions : WindowFunctions.values())
    // for (int radius = 1; radius <= 4; ++radius)
    // window(windowFunctions, radius);
    // subdiv3(); // manually edited 1 pic!
    // subdiv4a1();
    // subdiv4a2();
    // subdiv4b();
    // // custom();
    // decastL();
    // decastR();
  }
}
