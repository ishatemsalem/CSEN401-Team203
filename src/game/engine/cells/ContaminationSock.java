package game.engine.cells;

import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Dynamo;
import game.engine.monsters.Monster;
import game.engine.monsters.MultiTasker;
import game.engine.monsters.Schemer;

public class ContaminationSock extends TransportCell implements CanisterModifier {

	public ContaminationSock(String name, int effect) {
		super(name, effect);
	}
	



/* el goz2 el gdeed elly feeh skeleton */
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {

	    int finalValue = canisterValue;

	    if (monster instanceof Dynamo) {
	        finalValue *= 2;
	    } 
	    else if (monster instanceof MultiTasker) {
	        finalValue += 200;
	    } 
	    else if (monster instanceof Schemer) {
	        finalValue += 10;
	    }

	    monster.alterEnergy(finalValue);
	}
}

