// code by jph
package ch.ethz.idsc.owl.symlink;

import java.io.IOException;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.subdiv.curve.BSpline3CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.DeCasteljau;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenter;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

enum SymGenerate {
  ;
  public static void window(SmoothingKernel wf, int radius) throws IOException {
    TensorUnaryOperator tensorUnaryOperator = //
        GeodesicCenter.of(SymGeodesic.INSTANCE, wf);
    Tensor vector = Tensor.of(IntStream.range(0, 2 * radius + 1).mapToObj(SymScalar::of));
    Tensor tensor = tensorUnaryOperator.apply(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor);
    symLinkImage.title("" + wf.name() + "[" + (2 * radius + 1) + "]");
    ImageIO.write(symLinkImage.bufferedImage(), "png", UserHome.Pictures("export/" + wf.name().toLowerCase() + radius + ".png"));
  }

  public static void subdiv3() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 3).mapToObj(SymScalar::of));
    CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(SymGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.string(vector);
    {
      SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.Get(2));
      ImageIO.write(symLinkImage.bufferedImage(), "png", UserHome.Pictures("export/bspline3.png"));
    }
    {
      SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.Get(1));
      ImageIO.write(symLinkImage.bufferedImage(), "png", UserHome.Pictures("export/bspline3m.png"));
    }
  }

  public static void subdiv4a1() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 3).mapToObj(SymScalar::of));
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(SymGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.string(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.Get(1));
    ImageIO.write(symLinkImage.bufferedImage(), "png", UserHome.Pictures("export/bspline4a1.png"));
  }

  public static void subdiv4a2() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 3).mapToObj(SymScalar::of));
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.split2(SymGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.string(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.Get(1));
    ImageIO.write(symLinkImage.bufferedImage(), "png", UserHome.Pictures("export/bspline4a2.png"));
  }

  public static void subdiv4b() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 3).mapToObj(SymScalar::of));
    CurveSubdivision curveSubdivision = //
        BSpline4CurveSubdivision.split3(SymGeodesic.INSTANCE, RationalScalar.HALF);
    Tensor tensor = curveSubdivision.string(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.Get(1));
    ImageIO.write(symLinkImage.bufferedImage(), "png", UserHome.Pictures("export/bspline4b.png"));
  }

  public static void custom() throws IOException {
    Scalar s0 = SymScalar.of(0);
    Scalar s1 = SymScalar.of(1);
    Scalar s2 = SymScalar.of(2);
    Scalar s3 = SymScalar.of(s0, s1, RealScalar.of(2));
    Scalar s4 = SymScalar.of(s3, s2, RationalScalar.of(1, 3));
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) s4);
    ImageIO.write(symLinkImage.bufferedImage(), "png", UserHome.Pictures("export/custom.png"));
  }

  public static void decastL() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 4).mapToObj(SymScalar::of));
    DeCasteljau deCasteljau = new DeCasteljau(SymGeodesic.INSTANCE, vector);
    SymScalar symScalar = (SymScalar) deCasteljau.apply(RationalScalar.of(1, 3));
    SymLinkImage symLinkImage = new SymLinkImage(symScalar);
    ImageIO.write(symLinkImage.bufferedImage(), "png", UserHome.Pictures("export/decastel41_3.png"));
  }

  public static void decastR() throws IOException {
    Tensor vector = Tensor.of(IntStream.range(0, 4).mapToObj(SymScalar::of));
    DeCasteljau deCasteljau = new DeCasteljau(SymGeodesic.INSTANCE, vector);
    SymScalar symScalar = (SymScalar) deCasteljau.apply(RationalScalar.of(3, 4));
    SymLinkImage symLinkImage = new SymLinkImage(symScalar);
    ImageIO.write(symLinkImage.bufferedImage(), "png", UserHome.Pictures("export/decastel43_4.png"));
  }

  public static void main(String[] args) throws IOException {
    window(SmoothingKernel.GAUSSIAN, 5);
    window(SmoothingKernel.GAUSSIAN, 6);
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
