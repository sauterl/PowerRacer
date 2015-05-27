/**
 * 
 */
package server.game.powerup;

import server.game.GameLogic;
import shared.game.PowerRacerGame;
import shared.game.powerup.Boost;
import shared.game.powerup.Powerup;
import shared.game.powerup.PowerupBox;

/**
 * @author marco
 *
 */
public class PowerupLogic {

	public static void spawnNewPowerupBox(PowerRacerGame game) {
		double x = 1000;
		double y = 1000;
		int ID = game.getPowerupManager().getNewID();
		PowerupBox pB = new PowerupBox(x, y, ID);
		StringBuilder packet = new StringBuilder();
		packet.append("GSPBI:");
		packet.append(x);
		packet.append(":");
		packet.append(y);
		packet.append(":");
		packet.append(ID);
		packet.append(":");
		GameLogic.sendPacketToAllPlayers(game, packet.toString());
		game.getPowerupManager().getCollidablelist().add(pB);
	}

}
