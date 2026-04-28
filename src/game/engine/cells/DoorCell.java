package game.engine.cells;

import game.engine.Board;
import game.engine.Role;
import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class DoorCell extends Cell implements CanisterModifier {
	private Role role;
	private int energy;
	private boolean activated;
	
	public DoorCell(String name, Role role, int energy) {
		super(name);
		this.role = role;
		this.energy = energy;
		this.activated = false;
	}
	
	public Role getRole() {
		return role;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean isActivated) {
		this.activated = isActivated;
	}



	/* el goz2 el gdeed elly feeh skeleton */
	@Override
    public void onLand(Monster landingMonster, Monster opponentMonster) {
        super.onLand(landingMonster, opponentMonster);
        
        if (!isActivated()) { 
            int modifier = (this.role == landingMonster.getRole()) ? this.energy : -this.energy;
            boolean stateChanged = false;

            int initialCanister = landingMonster.getEnergy();
            modifyCanisterEnergy(landingMonster, modifier);
            if (landingMonster.getEnergy() != initialCanister) stateChanged = true;

            for (Monster stationed : Board.getStationedMonsters()) {
                if (stationed.getRole() == landingMonster.getRole()) {
                    int initialStationed = stationed.getEnergy();
                    modifyCanisterEnergy(stationed, modifier);
                    if (stationed.getEnergy() != initialStationed) stateChanged = true;
                }
            }

            if (stateChanged) {
                setActivated(true);
            }
        }
    }

    @Override
    public void modifyCanisterEnergy(Monster monster, int canisterValue) {
        if (canisterValue < 0 && monster.isShielded()) {
            monster.setShielded(false); 
        } else {
            monster.setEnergy(monster.getEnergy() + canisterValue);
        }
    }
}
