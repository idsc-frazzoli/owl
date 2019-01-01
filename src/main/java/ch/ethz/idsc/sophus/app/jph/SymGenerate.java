// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.sophus.curve.BSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.curve.BezierFunction;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.symlink.SymGeodesic;
import ch.ethz.idsc.sophus.symlink.SymLinkImage;
import ch.ethz.idsc.sophus.symlink.SymLinkImages;
import ch.ethz.idsc.sophus.symlink.SymScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/* package */ enum SymGenerate {
  ;
  public static void subdiv3() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 3).mapToObj(SymScalar::leaf));
    CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(SymGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.string(vector);
    {
      SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.Get(2));
      ImageIO.write(symLinkImage.bufferedImageCropped(true), "png", UserHome.Pictures("export/bspline3.png"));
    }
    {
      SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.Get(1));
      ImageIO.write(symLinkImage.bufferedImageCropped(true), "png", UserHome.Pictures("export/bspline3m.png"));
    }
  }

  public static void subdiv4a1() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 3).mapToObj(SymScalar::leaf));
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(SymGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.string(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.Get(1));
    ImageIO.write(symLinkImage.bufferedImageCropped(true), "png", UserHome.Pictures("export/bspline4a1.png"));
  }

  public static void subdiv4a2() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 3).mapToObj(SymScalar::leaf));
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.split2(SymGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.string(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.Get(1));
    ImageIO.write(symLinkImage.bufferedImageCropped(true), "png", UserHome.Pictures("export/bspline4a2.png"));
  }

  public static void subdiv4b() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 3).mapToObj(SymScalar::leaf));
    CurveSubdivision curveSubdivision = //
        BSpline4CurveSubdivision.split3(SymGeodesic.INSTANCE, RationalScalar.HALF);
    Tensor tensor = curveSubdivision.string(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.Get(1));
    ImageIO.write(symLinkImage.bufferedImageCropped(true), "png", UserHome.Pictures("export/bspline4b.png"));
  }

  public static void custom() throws IOException {
    Scalar s0 = SymScalar.leaf(0);
    Scalar s1 = SymScalar.leaf(1);
    Scalar s2 = SymScalar.leaf(2);
    Scalar s3 = SymScalar.of(s0, s1, RealScalar.of(2));
    Scalar s4 = SymScalar.of(s3, s2, RationalScalar.of(1, 3));
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) s4);
    ImageIO.write(symLinkImage.bufferedImageCropped(true), "png", UserHome.Pictures("export/custom.png"));
  }

  public static void decastL() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 4).mapToObj(SymScalar::leaf));
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(SymGeodesic.INSTANCE, vector);
    SymScalar symScalar = (SymScalar) scalarTensorFunction.apply(RationalScalar.of(1, 3));
    SymLinkImage symLinkImage = new SymLinkImage(symScalar);
    ImageIO.write(symLinkImage.bufferedImageCropped(true), "png", UserHome.Pictures("export/decastel41_3.png"));
  }

  public static void decastR() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 4).mapToObj(SymScalar::leaf));
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(SymGeodesic.INSTANCE, vector);
    SymScalar symScalar = (SymScalar) scalarTensorFunction.apply(RationalScalar.of(3, 4));
    SymLinkImage symLinkImage = new SymLinkImage(symScalar);
    ImageIO.write(symLinkImage.bufferedImageCropped(true), "png", UserHome.Pictures("export/decastel43_4.png"));
  }

  public static void main(String[] args) throws IOException {
    {
      SymLinkImage symLinkImage = SymLinkImages.smoothingKernel(SmoothingKernel.GAUSSIAN, 2);
      BufferedImage bufferedImage = symLinkImage.bufferedImageCropped(true);
      ImageIO.write(bufferedImage, "png", UserHome.Pictures("gaussian23.png"));
    }
    // BufferedImage bufferedImage =
    SymLinkImages.smoothingKernel(SmoothingKernel.GAUSSIAN, 5);
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
