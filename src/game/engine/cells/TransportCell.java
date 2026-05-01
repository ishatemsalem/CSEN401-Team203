package game.engine.cells;

import game.engine.Constants;
import game.engine.monsters.Monster;

public abstract class TransportCell extends Cell {
	private int effect;

	public TransportCell(String name, int effect) {
		super(name);
		this.effect = effect;
	}

	public int getEffect() {
		return effect;
	}

	

	/* el goz2 el gdeed elly feeh skeleton */
	public void transport(Monster monster) {
		int newPos = (monster.getPosition() + this.getEffect()) % Constants.BOARD_SIZE;
		if (newPos < 0) {
			newPos += Constants.BOARD_SIZE;
		}
		monster.setPosition(newPos);
	}

    @Override
	public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);
		transport(landingMonster);
	}
}
