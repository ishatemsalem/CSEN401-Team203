package game.engine.cells;

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
    }
}
