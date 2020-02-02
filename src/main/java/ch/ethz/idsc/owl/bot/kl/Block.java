// code by jph
package ch.ethz.idsc.owl.bot.kl;

/* package */ enum Block {
  _b22(2, 2), //
  _b21(2, 1), //
  _b12(1, 2), //
  _b11(1, 1), //
  _b31(3, 1), //
  _cd2(2, 2), //
  _cu2(2, 2), //
  ;

  public final int wx;
  public final int wy;

  Block(int wx, int wy) {
    this.wx = wx;
    this.wy = wy;
  }
}
