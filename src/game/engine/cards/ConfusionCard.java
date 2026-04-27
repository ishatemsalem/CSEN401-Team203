package game.engine.cards;

import game.engine.monsters.Monster;

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
	public void performAction(Monster player, Monster opponent) {
    }
}
