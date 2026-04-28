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
@Override
    public void performAction(Monster player, Monster opponent) {
        int stolen = Math.min(this.getEnergy(), opponent.getEnergy());

        if (opponent.isShielded()) {
            opponent.setShielded(false);
            stolen = 0;
        } else {
            opponent.setEnergy(opponent.getEnergy() - stolen);
        }

        player.setEnergy(player.getEnergy() + stolen);

        modifyCanisterEnergy(player, stolen);
        modifyCanisterEnergy(opponent, -stolen);
    }

    @Override
    public void modifyCanisterEnergy(Monster monster, int canisterValue) {
        monster.setEnergy(monster.getEnergy() + canisterValue);
    }
}
