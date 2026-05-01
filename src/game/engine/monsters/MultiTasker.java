package game.engine.monsters;

import game.engine.Role;
import game.engine.Constants;


public class MultiTasker extends Monster {
	private int normalSpeedTurns;
	
	public MultiTasker(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
		this.normalSpeedTurns = 0;
	}

	public int getNormalSpeedTurns() {
		return normalSpeedTurns;
	}

	public void setNormalSpeedTurns(int normalSpeedTurns) {
		this.normalSpeedTurns = normalSpeedTurns;
	}


	/* el goz2 el gdeed elly feeh skeleton */
	@Override
	public void executePowerupEffect(Monster opponentMonster) {
    	normalSpeedTurns = 2;
	}

	@Override
    public void move(int distance) {
        if (normalSpeedTurns > 0) {
            normalSpeedTurns--;
            super.move(distance);
        } else {
            super.move(distance / 2);
        }
    }
	
	public void setEnergy(int energy) {
	    int change = energy - this.getEnergy();
	    if (change >= 0)
	        super.setEnergy(this.getEnergy() + change + Constants.MULTITASKER_BONUS);
	    else
	        super.setEnergy(this.getEnergy() + change + Constants.MULTITASKER_BONUS);
	}

	
}