package game.engine.cells;

import game.engine.Constants;
import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class ContaminationSock extends TransportCell implements CanisterModifier {

	public ContaminationSock(String name, int effect) {
		super(name, effect);
	}
	



/* el goz2 el gdeed elly feeh skeleton */
	@Override
    public void onLand(Monster landingMonster, Monster opponentMonster) {
        super.onLand(landingMonster, opponentMonster); 
        modifyCanisterEnergy(landingMonster, Constants.SLIP_PENALTY);
    }

    @Override
    public void modifyCanisterEnergy(Monster monster, int canisterValue) {
        monster.setEnergy(monster.getEnergy() + canisterValue);
    }

}

