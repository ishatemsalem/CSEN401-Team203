package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import game.engine.cells.Cell;
import game.engine.dataloader.DataLoader;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
/* el goz2 el gdeed elly feeh skeleton, imports edition*/
import game.engine.monsters.Monster;

public class Game{

	private Board board;
	private ArrayList<Monster> allMonsters; 
	private Monster player;
	private Monster opponent;
	private Monster current;
	
	public Game(Role playerRole) throws IOException {
	
	this.board = new Board(DataLoader.readCards());
    this.allMonsters = DataLoader.readMonsters();
    this.player = selectRandomMonsterByRole(playerRole);
    this.opponent = selectRandomMonsterByRole(playerRole == Role.SCARER ? Role.LAUGHER : Role.SCARER);
    this.current = player;
    
	this.allMonsters.remove(this.player);
    this.allMonsters.remove(this.opponent);
	
    ArrayList<Monster> list = new ArrayList<>(this.allMonsters);
    list.remove(this.player);
    list.remove(this.opponent);
    Board.setStationedMonsters(list);
    
    ArrayList<Cell> specialCells = DataLoader.readCells();
    this.board.initializeBoard(specialCells);
	}
	
	public Board getBoard() {
		return board;
	}
	
	public ArrayList<Monster> getAllMonsters() {
		return allMonsters; 
	}
	
	public Monster getPlayer() {
		return player;
	}
	
	public Monster getOpponent() {
		return opponent;
	}
	
	public Monster getCurrent() {
		return current;
	}
	
	public void setCurrent(Monster current) {
		this.current = current;
	}
	
	private Monster selectRandomMonsterByRole(Role role) {
		Collections.shuffle(allMonsters);
	    return allMonsters.stream()
	    		.filter(m -> m.getRole() == role)
	    		.findFirst()
	    		.orElse(null);
	}

	




	/* el goz2 el gdeed elly feeh skeleton*/

private Monster getCurrentOpponent() {
		if (this.getCurrent() == this.getPlayer()) {
			return this.getOpponent();
		} else {
			return this.getPlayer();
		}		
	}
	
	private int rollDice() {
		int x=(int)((Math.random()*6)+1);
		return x;
	}
	public void usePowerup() throws OutOfEnergyException{
		if(this.getCurrent().getEnergy() < Constants.POWERUP_COST) {
			throw new OutOfEnergyException();
		}
		else {
			int x=this.getCurrent().getEnergy() - Constants.POWERUP_COST;
			this.getCurrent().setEnergy(x);
			this.getCurrent().executePowerupEffect(this.getCurrentOpponent());
			}
	}
	public void playTurn() throws InvalidMoveException{
		if(getCurrent().isFrozen()) {
			getCurrent().setFrozen(false);
		}
		else {
			int count= this.rollDice();
			this.getBoard().moveMonster(this.getCurrent(), count, this.getCurrentOpponent());
			}
		this.switchTurn();
		}
	private void switchTurn() {
		if (this.getCurrent() == this.getPlayer()) {
			this.setCurrent(this.getOpponent());
		} else {
			this.setCurrent(this.getPlayer());
		}		
	}
	private boolean checkWinCondition(Monster monster) {
		if(monster.getEnergy()>= Constants.WINNING_ENERGY && monster.getPosition() == Constants.WINNING_POSITION){
			return true;
		}
		return false;
	}
	public Monster getWinner() {
		if(checkWinCondition(this.getPlayer())) {
			return this.getPlayer();
		}
		if(checkWinCondition(this.getOpponent())){
			return this.getOpponent();
		}

		return null;
	
	}
}	
		