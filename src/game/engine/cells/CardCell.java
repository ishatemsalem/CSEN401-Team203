package game.engine.cells;

import game.engine.Board;
import game.engine.monsters.Monster;

/* */
import game.engine.cards.Card;       // Add this import

public class CardCell extends Cell {
	
	public CardCell(String name) {
        super(name);
    }
   @Override
    public void onLand(Monster landingMonster, Monster opponentMonster) {
        super.onLand(landingMonster, opponentMonster);
        Card drawn = Board.drawCard();
        if (drawn != null) {
            drawn.performAction(landingMonster, opponentMonster);
        }
    }
}
