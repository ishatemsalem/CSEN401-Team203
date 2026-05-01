package game.engine.cards;

import game.engine.monsters.Monster;

/* */
import game.engine.Role;

public class ConfusionCard extends Card {
	private int duration;
	
	public ConfusionCard(String name, String description, int rarity, int duration) {
		super(name, description, rarity, false);
		this.duration = duration;
	}
	
	public int getDuration() {
		return duration;
	}


	/* el goz2 el gdeed elly feeh skeleton */
	@Override
    public void performAction(Monster player, Monster opponent) {
        Role temp = player.getRole();
        player.setRole(opponent.getRole());
        opponent.setRole(temp);

        // set confusion turns to card's duration, not accumulate — prevents stacking if confusion is reapplied
        player.setConfusionTurns(getDuration());
        opponent.setConfusionTurns(getDuration());
    }
}
