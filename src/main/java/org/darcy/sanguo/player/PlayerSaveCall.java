package org.darcy.sanguo.player;

public class PlayerSaveCall implements Runnable {
	private Player player;

	public PlayerSaveCall(Player player) {
		this.player = player;
	}

	public void run() {
		this.player.save();
	}
}
