package game.engine.cells;

import game.engine.monsters.Monster;

public class Cell {
	private String name;
	private Monster monster; 
	
	public Cell(String name) {
		this.name = name;
		this.monster = null;
	}

	public String getName() {
		return name;
	}
	
	public Monster getMonster() {
		return monster;
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}



	/* el goz2 el gdeed elly feeh skeleton */
	public boolean isOccupied() {
        return false;
    }

    public void onLand(Monster landingMonster, Monster opponentMonster) {
    }

	public void setName(String name) {
    this.name = name;
}
}
