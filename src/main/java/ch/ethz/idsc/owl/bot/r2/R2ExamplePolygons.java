// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum R2ExamplePolygons {
  ;
  public static final Tensor CORNER_TOP_LEFT = Tensors.matrix(new Number[][] { //
      { 3, 0 }, //
      { 4, 0 }, //
      { 4, 4 }, //
      { 1, 4 }, //
      { 1, 3 }, //
      { 3, 3 } //
  }).unmodifiable();
  // ---
  public static final Tensor CORNER_POINTY = Tensors.matrix(new Number[][] { //
      { 3, 0 }, //
      { 4, 0 }, //
      { 6, 2 }, //
      { 4, 4 }, //
      { 1, 4 }, //
      { 1, 3 }, //
      { 3, 3 } //
  }).unmodifiable();
  // ---
  public static final Tensor BULKY_TOP_LEFT = Tensors.matrix(new Number[][] { //
      { 3, 0 }, //
      { 4, 0 }, //
      { 4, 6 }, //
      { 1, 6 }, //
      { 1, 3 }, //
      { 3, 3 } //
  }).unmodifiable();
  // ---
  public static final Tensor CORNER_CENTERED = Tensors.matrix(new Number[][] { //
      { 0, 0 }, //
      { 0, -1 }, //
      { 1, -1 }, //
      { 1, 1 }, //
      { -1, 1 }, //
      { -1, 0 } //
  }).unmodifiable();
  // ---
}
