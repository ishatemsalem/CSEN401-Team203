package game.engine.cells;

import game.engine.Role;
import game.engine.monsters.Monster;

import game.engine.interfaces.CanisterModifier;



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
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
		
        if (monster == null)
            throw new IllegalArgumentException("Monster can't be null");

        monster.alterEnergy(canisterValue);
    }
}
