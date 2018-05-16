package server.game.powerup;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import shared.game.PowerRacerGame;
import shared.game.powerup.Collidable;

public class PowerupManager {

	private PowerRacerGame game;
	public LinkedList<Collidable> collidablelist;
	public ConcurrentLinkedQueue<Integer> collidableIDlist;
	Random rand;

	/**
	 * @param game
	 *            the relevant game object
	 */

	public PowerupManager(PowerRacerGame game) {
		this.setGame(game);
		rand = new Random();
		collidablelist = new LinkedList<Collidable>();
		collidableIDlist = new ConcurrentLinkedQueue<Integer>();
	}

	/**
	 * @return the game
	 */
	public PowerRacerGame getGame() {
		return game;
	}

	/**
	 * @param game
	 *            the game to set
	 */
	public void setGame(PowerRacerGame game) {
		this.game = game;
	}

	public int getNewID() {
		while (true) {
			out: while (true) {
				int tempID = rand.nextInt(2000) + 1000;
				for (int i = 0; i < collidablelist.size(); i++) {
					if (tempID == collidablelist.get(i).getID()) {
						break out;
					}
				}
				collidableIDlist.add(tempID);
				return tempID;
			}
		}
	}

	public LinkedList<Collidable> getCollidablelist() {
		return this.collidablelist;
	}

	public void setCollidablelist(LinkedList<Collidable> collidablelist) {
		this.collidablelist = collidablelist;
	}

	public ConcurrentLinkedQueue<Integer> getCollidableIDlist() {
		return collidableIDlist;
	}

	public void setCollidableIDlist(
			ConcurrentLinkedQueue<Integer> collidableIDlist) {
		this.collidableIDlist = collidableIDlist;
	}

}
