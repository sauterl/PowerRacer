package shared.game.model;

import client.gui.RandomMapGenerator;

/**
 * Abstract representation of a racetrack.
 * Ultimately, {@link shared.game.VisualRaceTrack}'s are based on {@link RaceTrackModel}s.
 *
 * @author loris.sauter
 */
public class RaceTrackModel {

  /**
   * The number of different cars
   */
  public static final int NUMBER_OF_CARS = 5;

  /**
   * A unique track name
   */
  private String trackName;

  /**
   * The default tile ID
   */
  private byte defaultTile;
  /**
   * Starting positions
   */
  private int[][] startingPositions;

  /**
   * Item boxes
   */
  private int[][] itemBoxPositions;

  /**
   * The number of checkpoints
   */
  private int numbersOfCheckpoints;
  /**
   * The number of laps
   */
  private int numberOfLaps;

  private byte[][] map;

  private byte identifier;

  /**
   * Creates a random model based on the size
   * @param size
   * @return
   */
  public static RaceTrackModel createRandom(int size){
    RandomMapGenerator rmg = new RandomMapGenerator(size);
    return fromRandomGenerator(rmg);
  }

  public static RaceTrackModel createRandom(int size, byte id){
    RaceTrackModel m = createRandom(size);
    m.setIdentifier(id);
    return m;
  }

  /**
   * Creates a random model based on a seed
   * @param seed
   * @return
   */
  public static RaceTrackModel createRandom(String seed){
    return new RaceTrackModel(
        "RANDOM_GET",
        (byte)0,
        RandomMapGenerator.stringToStartingPositions(seed),
        RandomMapGenerator.stringToItemPositions(seed),
        RandomMapGenerator.stringToCheckpoints(seed),
        3,RandomMapGenerator.stringToTrack(seed)
    );
  }

  public static RaceTrackModel createRandom(String seed, byte id){
    RaceTrackModel m = createRandom(seed);
    m.setIdentifier(id);
    return m;
  }

  private static RaceTrackModel fromRandomGenerator(RandomMapGenerator rmg){
    return new RaceTrackModel(
        "RANDOM",
        (byte)0,
        rmg.getStartingPositions(),
        rmg.getItemPositions(),
        rmg.getCheckpoints(),3,rmg.getTrack());
  }

  public RaceTrackModel(String trackName, byte defaultTile, int[][] startingPositions,
      int numbersOfCheckpoints, int numberOfLaps, byte[][] map) {
    this(trackName, defaultTile, startingPositions, new int[][]{}, numbersOfCheckpoints, numberOfLaps, map);
  }

  public RaceTrackModel(String trackName, byte defaultTile, int[][] startingPositions,
      int[][] itemBoxPositions, int numbersOfCheckpoints, int numberOfLaps, byte[][] map) {
    this.trackName = trackName;
    this.defaultTile = defaultTile;
    this.startingPositions = startingPositions;
    this.itemBoxPositions = itemBoxPositions;
    this.numbersOfCheckpoints = numbersOfCheckpoints;
    this.numberOfLaps = numberOfLaps;
    this.map = map;
  }

  public RaceTrackModel(String trackName, byte defaultTile, int[][] startingPositions,
      int[][] itemBoxPositions, int numbersOfCheckpoints, int numberOfLaps, byte[][] map,
      byte identifier) {
    this.trackName = trackName;
    this.defaultTile = defaultTile;
    this.startingPositions = startingPositions;
    this.itemBoxPositions = itemBoxPositions;
    this.numbersOfCheckpoints = numbersOfCheckpoints;
    this.numberOfLaps = numberOfLaps;
    this.map = map;
    this.identifier = identifier;
  }

  public byte getIdentifier() {
    return identifier;
  }

  public void setIdentifier(byte identifier) {
    this.identifier = identifier;
  }

  public int[][] getItemBoxPositions() {
    return itemBoxPositions;
  }

  public void setItemBoxPositions(int[][] itemBoxPositions) {
    this.itemBoxPositions = itemBoxPositions;
  }

  public static int getNumberOfCars() {
    return NUMBER_OF_CARS;
  }

  public String getTrackName() {
    return trackName;
  }

  public void setTrackName(String trackName) {
    this.trackName = trackName;
  }

  public byte getDefaultTile() {
    return defaultTile;
  }

  public void setDefaultTile(byte defaultTile) {
    this.defaultTile = defaultTile;
  }

  public int[][] getStartingPositions() {
    return startingPositions;
  }

  public void setStartingPositions(int[][] startingPositions) {
    this.startingPositions = startingPositions;
  }

  public int getNumbersOfCheckpoints() {
    return numbersOfCheckpoints;
  }

  public void setNumbersOfCheckpoints(int numbersOfCheckpoints) {
    this.numbersOfCheckpoints = numbersOfCheckpoints;
  }

  public int getNumberOfLaps() {
    return numberOfLaps;
  }

  public void setNumberOfLaps(int numberOfLaps) {
    this.numberOfLaps = numberOfLaps;
  }

  public byte[][] getMap() {
    return map;
  }

  public void setMap(byte[][] map) {
    this.map = map;
  }
}
