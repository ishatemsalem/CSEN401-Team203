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
            if (landingMonster.getEnergy() > getCellMonster().getEnergy()) {
                int initialEnergy = landingMonster.getEnergy();
                int penalty = getCellMonster().getEnergy() - landingMonster.getEnergy();

                landingMonster.alterEnergy(penalty);
                getCellMonster().setEnergy(initialEnergy);
            }
        }
    }
}
