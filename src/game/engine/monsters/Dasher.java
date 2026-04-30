package game.engine.monsters;

import game.engine.Role;

public class Dasher extends Monster {
	private int momentumTurns;

	public Dasher(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
		this.momentumTurns = 0;
	}
	
	public int getMomentumTurns() {
		return momentumTurns;
	}
	
	public void setMomentumTurns(int momentumTurns) {
		this.momentumTurns = momentumTurns;
	}


	/* el goz2 el gdeed elly feeh skeleton */
	 @Override
	public void executePowerupEffect(Monster opponentMonster) {
    	momentumTurns = 3;
	}
	 @Override
	 public void move(int distance) {
        int multiplier = 2;

        if (momentumTurns > 0) {
            multiplier = 3;
            momentumTurns--;
        }

        super.move(distance * multiplier);
    }

}