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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TileModel tileModel = (TileModel) o;

    if (getIdentifier() != tileModel.getIdentifier()) {
      return false;
    }
    if (getSolid() != tileModel.getSolid()) {
      return false;
    }
    if (getCheckpointNumber() != tileModel.getCheckpointNumber()) {
      return false;
    }
    if (Double.compare(tileModel.getFrictionCoefficient(), getFrictionCoefficient()) != 0) {
      return false;
    }
    if (getTransparent() != tileModel.getTransparent()) {
      return false;
    }
    return getFileName().equals(tileModel.getFileName());
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = getIdentifier();
    result = 31 * result + getFileName().hashCode();
    result = 31 * result + (getSolid() ? 1 : 0);
    result = 31 * result + getCheckpointNumber();
    temp = Double.doubleToLongBits(getFrictionCoefficient());
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (getTransparent() ? 1 : 0);
    return result;
  }

  public void setIdentifier(byte identifier) {
    this.identifier = identifier;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setSolid(boolean solid) {
    this.solid = solid;
  }

  public void setCheckpointNumber(int checkpointNumber) {
    this.checkpointNumber = checkpointNumber;
  }

  public void setFrictionCoefficient(double frictionCoefficient) {
    this.frictionCoefficient = frictionCoefficient;
  }

  public void setTransparent(boolean transparent) {
    this.transparent = transparent;
  }
}
