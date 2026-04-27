package game.engine.monsters;

import game.engine.Role;

public class Schemer extends Monster {
	
	public Schemer(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}

	/* el goz2 el gdeed elly feeh skeleton */

	private int stealEnergyFrom(Monster target) {
        return 0;
    }
	
	public void executePowerupEffect(Monster opponentMonster) {
    }
}
