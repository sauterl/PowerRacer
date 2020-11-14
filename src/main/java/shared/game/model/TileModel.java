package shared.game.model;

/**
 * Private class for exclusive use when loading tiles to make setting tile properties more
 * controllable.
 *
 * @author Florian
 */
public class TileModel {

  private byte identifier;
  private String fileName;
  private boolean solid;
  private int checkpointNumber;
  private double frictionCoefficient;
  private boolean transparent;

  /**
   * Jackson/ Databind required default constructor
   */
  public TileModel(){

  }

  public TileModel(String fileName, boolean solid, int checkpointNumber,
      double frictionCoefficient, boolean transparent, int identifier) {
    this.fileName = fileName;
    this.solid = solid;
    this.checkpointNumber = checkpointNumber;
    this.frictionCoefficient = frictionCoefficient;
    this.transparent = transparent;
    this.identifier = (byte)identifier;
  }

  public String getFileName() {
    return fileName;
  }

  public boolean getSolid() {
    return solid;
  }

  public int getCheckpointNumber() {
    return checkpointNumber;
  }

  public double getFrictionCoefficient() {
    return frictionCoefficient;
  }

  public boolean getTransparent() {
    return transparent;
  }

  public byte getIdentifier() {
    return identifier;
  }
}
