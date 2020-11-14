package shared.game.presets;

import shared.game.model.TileModel;

/**
 * Preset Tiles
 *
 * @author loris.sauter
 */
public final class PresetTiles {


  public static final TileModel GRASS = new TileModel("grass", false, 0, 0.1, false, 0);
  public static final TileModel ASPHALT = new TileModel("asphalt", false, 0, 0.05, false, 1);
  public static final TileModel SAND = new TileModel("sand", false, 0, 0.15, false, 2);
  public static final TileModel SNOW = new TileModel("snow", false, 0, 0.125, false, 3);
  public static final TileModel CHECKPOINT_ONE = new TileModel("checkpointOne", false, 1, 0.05,
      false, 4);
  public static final TileModel CHECKPOINT_TWO = new TileModel("checkpointTwo", false, 2, 0.05,
      false, 5);
  public static final TileModel CHECKPOINT_THREE = new TileModel("checkpointThree", false, 3, 0.05,
      false, 6);
  public static final TileModel FINISH_LINE = new TileModel("finishLine", false, 99, 0.05, false,
      7);
  public static final TileModel CHECKPOINT_FOUR = new TileModel("checkpointFour", false, 4, 0.05,
      false, 8);
  public static final TileModel CHECKPOINT_FIVE = new TileModel("checkpointFive", false, 5, 0.05,
      false, 9);
  public static final TileModel CHECKPOINT_SIX = new TileModel("checkpointSix", false, 6, 0.05,
      false, 10);
  public static final TileModel CHECKPOINT_SEVEN = new TileModel("checkpointSeven", false, 7, 0.05,
      false, 11);
  public static final TileModel CHECKPOINT_EIGHT = new TileModel("checkpointEight", false, 8, 0.05,
      false, 12);
  public static final TileModel CHECKPOINT_NINE = new TileModel("checkpointNine", false, 9, 0.05,
      false, 13);
  public static final TileModel CHECKPOINT_TEN = new TileModel("checkpointTen", false, 10, 0.05,
      false, 14);
  public static final TileModel ASPHALT_UP = new TileModel("asphaltUp", false, 0, 0.05, false, 15);
  public static final TileModel ASPHALT_DOWN = new TileModel("asphaltDown", false, 0, 0.05, false,
      16);
  public static final TileModel ASPHALT_LEFT = new TileModel("asphaltLeft", false, 0, 0.05, false,
      17);
  public static final TileModel ASPHALT_RIGHT = new TileModel("asphaltRight", false, 0, 0.05, false,
      18);
  public static final TileModel ASPHALT_UP_RIGHT = new TileModel("asphaltUpRight", false, 0, 0.05,
      true, 19);
  public static final TileModel ASPHALT_UP_LEFT = new TileModel("asphaltUpLeft", false, 0, 0.05,
      true, 20);
  public static final TileModel ASPHALT_DOWN_RIGHT = new TileModel("asphaltDownRight", false, 0,
      0.05, true, 21);
  public static final TileModel ASPHALT_DOWN_LEFT = new TileModel("asphaltDownLeft", false, 0, 0.05,
      true, 22);
  public static final TileModel ICE = new TileModel("ice", false, 0, 0.125, false, 23);
  public static final TileModel ASPHALT_ICE_1 = new TileModel("asphalticeOne", false, 0, 0.055,
      true, 24);
  public static final TileModel ASPHALT_ICE_2 = new TileModel("asphalticeTwo", false, 0, 0.055,
      true, 25);
  public static final TileModel ASPHALT_ICE_3 = new TileModel("asphalticeThree", false, 0, 0.055,
      true, 26);
  public static final TileModel ASPHALT_ICE_4 = new TileModel("asphalticeFour", false, 0, 0.055,
      true, 27);
  public static final TileModel BOOST_TILE = new TileModel("BoostTile", false, 0, -0.05, false, 28);
  // public static final TileModel model = new TileModel("horzWall", true, 0, 0, false)

  public static final TileModel[] TILES = {
      GRASS,
      ASPHALT,
      SAND,
      SNOW,
      CHECKPOINT_ONE,
      CHECKPOINT_TWO,
      CHECKPOINT_THREE,
      FINISH_LINE,
      CHECKPOINT_FOUR,
      CHECKPOINT_FIVE,
      CHECKPOINT_SIX,
      CHECKPOINT_SEVEN,
      CHECKPOINT_EIGHT,
      CHECKPOINT_NINE,
      CHECKPOINT_TEN,
      ASPHALT_UP,
      ASPHALT_DOWN,
      ASPHALT_LEFT,
      ASPHALT_RIGHT,
      ASPHALT_UP_RIGHT,
      ASPHALT_UP_LEFT,
      ASPHALT_DOWN_RIGHT,
      ASPHALT_DOWN_LEFT,
      ICE,
      ASPHALT_ICE_1,
      ASPHALT_ICE_2,
      ASPHALT_ICE_3,
      ASPHALT_ICE_4,
      BOOST_TILE
  };


  private PresetTiles() {/* No ctor needed */}


}
