package game.engine.cells;

import game.engine.monsters.*;

public class MonsterCell extends Cell {
	private Monster cellMonster;

	public MonsterCell(String name, Monster cellMonster) {
		super(name);
		this.cellMonster = cellMonster;
	}

	public Monster getCellMonster() {
		return cellMonster;
	}

	@Override
	public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);
				
		if (getCellMonster().getRole() == landingMonster.getRole()) {
			landingMonster.executePowerupEffect(opponentMonster);
		} else {
			int landingEnergy = landingMonster.getEnergy();
			int cellEnergy = getCellMonster().getEnergy();
			if (landingEnergy > cellEnergy) {
				landingMonster.alterEnergy(cellEnergy - landingEnergy);
				getCellMonster().alterEnergy(landingEnergy - cellEnergy);
			}
		}
	}
}
