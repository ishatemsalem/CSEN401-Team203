package game.engine.cards;

import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class EnergyStealCard extends Card implements CanisterModifier {
	private int energy;

	public EnergyStealCard(String name, String description, int rarity, int energy) {
		super(name, description, rarity, true);
		this.energy = energy;
	}
	
	public int getEnergy() {
		return energy;
	}
	

	/* el goz2 el gdeed elly feeh skeleton */
	public void performAction(Monster player, Monster opponent) {
    }
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
    }
}
