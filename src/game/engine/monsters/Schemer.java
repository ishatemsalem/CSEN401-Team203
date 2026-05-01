package game.engine.monsters;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Role;

public class Schemer extends Monster {
	
	public Schemer(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}

	/* el goz2 el gdeed elly feeh skeleton */
	private int stealEnergyFrom(Monster target) {  
		int amount = Math.min(Constants.SCHEMER_STEAL, target.getEnergy());
	    target.setEnergy(target.getEnergy() - amount);  //"Not affected by the shield", use setEnergy directly not alterEnergy so that passive bonuses arent triggered
	    return amount;
	} 
	@Override
	public void executePowerupEffect(Monster opponentMonster) {
		int total = 0;
		total += stealEnergyFrom(opponentMonster);
		for (Monster m : Board.getStationedMonsters()) {
			total += stealEnergyFrom(m);
		}
		this.alterEnergy(total);
	} 
	
	public void setEnergy(int energy) {
	    int change = energy - this.getEnergy();
	    super.setEnergy(this.getEnergy() + change + Constants.SCHEMER_STEAL);
	}
}

	
