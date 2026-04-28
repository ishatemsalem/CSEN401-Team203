package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


import game.engine.dataloader.DataLoader;
import game.engine.monsters.*;


/* el goz2 el gdeed elly feeh skeleton, imports edition*/
import game.engine.monsters.Monster;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.exceptions.InvalidMoveException;




public class Game {
import game.engine.exceptions.InvalidMoveException;

import game.engine.exceptions.OutOfEnergyException;
import game.engine.dataloader.DataLoader;
import game.engine.monsters.*;

public class Game{

	private Board board;
	private ArrayList<Monster> allMonsters; 
	private Monster player;
	private Monster opponent;
	private Monster current;
	
	public Game(Role playerRole) throws IOException {
		this.board = new Board(DataLoader.readCards());

		ArrayList<Monster> list= Board.getStationedMonsters();

		this.allMonsters = DataLoader.readMonsters();
		
		this.player = selectRandomMonsterByRole(playerRole);
		this.opponent = selectRandomMonsterByRole(playerRole == Role.SCARER ? Role.LAUGHER : Role.SCARER);
		this.current = player;

		list.remove(this.player);
		list.remove(this.opponent);
		Board.setStationedMonsters(list);
		ArrayList<Cell> specialCells = DataLoader.readCells();
		initializeBoard(specialCells);
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
        return null;
    }

    private int rollDice() {
        return 0;
    }

    public void usePowerup() throws OutOfEnergyException {
    }

    public void playTurn() throws InvalidMoveException {
    }

    private void switchTurn() {
    }

    private boolean checkWinCondition(Monster monster) {
        return false;
    }

    public Monster getWinner() {
        return null;
    }

	private Monster getCurrentOpponent(){
		if(this.getCurrent().getRole()==this.getOpponent().getRole()){
			return getPlayer();
		}
		else {
			return getOpponent();
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
	
			}
	}
	public void playTurn() throws InvalidMoveException{
		if(getCurrent().isFrozen()) {
			getCurrent().setFrozen(false);
		}
		else {
			int count= this.rollDice();
			int position= this.getCurrent().getPosition();
			int newpos= position +count;
			this.getCurrent().setPosition(newpos);
			}
		this.switchTurn();
		}
	private void switchTurn() {
			if(this.getPlayer().getRole()==this.getCurrent().getRole()) {
				this.setCurrent(this.getOpponent());
			}
			else {
				this.setCurrent(this.getPlayer());
			}		
	}
	private boolean checkWinCondition(Monster monster) {
		if(monster.getEnergy()== Constants.WINNING_ENERGY && monster.getPosition() >= Constants.WINNING_POSITION){
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
		