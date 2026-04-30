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
			boolean stateChanged = false;
			int initialCanister = landingMonster.getEnergy();
			boolean penaltyBlocked = landingMonster.isShielded() && (this.role != landingMonster.getRole());

			modifyCanisterEnergy(landingMonster, this.energy);
			if (landingMonster.getEnergy() != initialCanister) {
				stateChanged = true;}

			if (!penaltyBlocked) {
				for (Monster stationed : Board.getStationedMonsters()) {
					if (stationed.getRole() == landingMonster.getRole()) {
						int initialStationed = stationed.getEnergy();
						modifyCanisterEnergy(stationed, this.energy);
						if (stationed.getEnergy() != initialStationed) {
							stateChanged = true;}}}}

			if (stateChanged) {
				setActivated(true);
			}}
}

    @Override
    public void modifyCanisterEnergy(Monster monster, int canisterValue) {
		int modifier = (this.role == monster.getRole()) ? canisterValue : -canisterValue;
    	monster.alterEnergy(modifier);
    }
}
