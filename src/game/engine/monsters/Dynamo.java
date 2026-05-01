package game.engine.monsters;

import game.engine.Role;

public class Dynamo extends Monster {
	
	public Dynamo(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}
	


	/* el goz2 el gdeed elly feeh skeleton*/
	@Override
	public void executePowerupEffect(Monster opponentMonster) {
		opponentMonster.setFrozen(true);
    }
	
	public void setEnergy(int energy) {
	    int change = energy - this.getEnergy(); // derive the change
	    super.setEnergy(this.getEnergy() + change * 2); //Doubles all incoming energy changes, whether positive or negative.
	}
	
}
