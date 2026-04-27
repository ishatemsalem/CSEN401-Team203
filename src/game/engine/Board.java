package game.engine;

import java.util.ArrayList;
import java.util.Collections;

import game.engine.cards.Card;
import game.engine.cells.*;
import game.engine.monsters.Monster;



/* el goz2 el gdeed elly feeh skeleton, imports edition*/
import java.util.ArrayList;

import game.engine.cards.Card;
import game.engine.monsters.Monster;
import game.engine.exceptions.InvalidMoveException;




public class Board {
	private Cell[][] boardCells;
	private static ArrayList<Monster> stationedMonsters; 
	private static ArrayList<Card> originalCards;
	public static ArrayList<Card> cards;
	
	public Board(ArrayList<Card> readCards) {
		this.boardCells = new Cell[Constants.BOARD_ROWS][Constants.BOARD_COLS];
		stationedMonsters = new ArrayList<Monster>();
		originalCards = readCards;
		cards = new ArrayList<Card>();
	}
	
	public Cell[][] getBoardCells() {
		return boardCells;
	}
	
	public static ArrayList<Monster> getStationedMonsters() {
		return stationedMonsters;
	}
	
	public static void setStationedMonsters(ArrayList<Monster> stationedMonsters) {
		Board.stationedMonsters = stationedMonsters;
	}

	public static ArrayList<Card> getOriginalCards() {
		return originalCards;
	}
	
	public static ArrayList<Card> getCards() {
		return cards;
	}
	
	public static void setCards(ArrayList<Card> cards) {
		Board.cards = cards;
	}


	/* el goz2 el gdeed elly feeh skeleton, imports edition*/

	private int[] indexToRowCol(int index) {
        
		int row = index / Constants.BOARD_COLS;
		int col = index % Constants.BOARD_COLS;
		if (row%2 != 0) 
			col = Constants.BOARD_COLS - 1 - col;     // For zigzag pattern, reverse column index for odd rows
		return new int[]{row, col};

    }

    private Cell getCell(int index) {
        int[] rowCol = indexToRowCol(index);
		return boardCells[rowCol[0]][rowCol[1]];
    }

    private void setCell(int index, Cell cell) {
		int[] rowCol = indexToRowCol(index);
		boardCells[rowCol[0]][rowCol[1]] = cell;
    }


	//this method will be the end of me
    public void initializeBoard(ArrayList<Cell> specialCells) {
		int specialListPointer = 0;
		for (int i = 0; i < 100; i++) {
    		if (i % 2 == 0) {
            	setCell(i, new Cell("Normal"));
        	} else {
            	setCell(i, specialCells.get(specialListPointer++));
        	}
    	}
		// Overwrite Card Cells
		for (int i = 0; i < Constants.CARD_CELL_INDICES.length; i++) {
			setCell(Constants.CARD_CELL_INDICES[i], specialCells.get(specialListPointer++));
		}

		// Overwrite Conveyor Belts
		for (int i = 0; i < Constants.CONVEYOR_CELL_INDICES.length; i++) {
			setCell(Constants.CONVEYOR_CELL_INDICES[i], specialCells.get(specialListPointer++));
		}

		// Overwrite Contamination Socks
		for (int i = 0; i < Constants.SOCK_CELL_INDICES.length; i++) {
			setCell(Constants.SOCK_CELL_INDICES[i], specialCells.get(specialListPointer++));
		}

		// Overwrite Monster Cells and assign stationed monsters
		
		
		for (int i = 0; i < Constants.MONSTER_CELL_INDICES.length; i++) {
			specialListPointer++; // skip the placeholder MonsterCell from CSV
			Monster monster = getStationedMonsters().get(i);
			monster.move(Constants.MONSTER_CELL_INDICES[i]); // set monster's position
			MonsterCell mCell = new MonsterCell(monster.getName(), monster); // name synced automatically
			setCell(Constants.MONSTER_CELL_INDICES[i], mCell);
		}
    }


	public void initializeBoard2(ArrayList<Cell> specialCells) {
		//Separate the CSV-loaded cells by type
		ArrayList<DoorCell> doorCells = new ArrayList<>();
		ArrayList<ConveyorBelt> conveyorBelts = new ArrayList<>();
		ArrayList<ContaminationSock> contaminationSocks = new ArrayList<>();

		for (Cell cell : specialCells) {
			if (cell instanceof DoorCell) {
				doorCells.add((DoorCell) cell);
			} else if (cell instanceof ConveyorBelt) {
				conveyorBelts.add((ConveyorBelt) cell);
			} else if (cell instanceof ContaminationSock) {
				contaminationSocks.add((ContaminationSock) cell);
			}
		}

		// Step 1: Base layer — even → Normal, odd → DoorCell
		int doorIndex = 0;
		for (int i = 0; i < Constants.BOARD_SIZE; i++) {
			if (i % 2 == 0) {
				setCell(i, new Cell("Normal Cell " + i));
			} else {
				setCell(i, doorCells.get(doorIndex++));
			}
		}

		// Step 2: Override with CardCells at their designated indices
		for (int index : Constants.CARD_CELL_INDICES) {
			setCell(index, new CardCell("Card Cell " + index));
		}

		// Step 3: Override with ConveyorBelts at their designated indices
		for (int i = 0; i < Constants.CONVEYOR_CELL_INDICES.length; i++) {
			setCell(Constants.CONVEYOR_CELL_INDICES[i], conveyorBelts.get(i));
		}

		// Step 4: Override with ContaminationSocks at their designated indices
		for (int i = 0; i < Constants.SOCK_CELL_INDICES.length; i++) {
			setCell(Constants.SOCK_CELL_INDICES[i], contaminationSocks.get(i));
		}

		// Step 5: Override with MonsterCells, assigning each stationed monster its position
		for (int i = 0; i < Constants.MONSTER_CELL_INDICES.length; i++) {
			int cellIndex = Constants.MONSTER_CELL_INDICES[i];
			Monster stationedMonster = stationedMonsters.get(i);
			stationedMonster.setPosition(cellIndex);
			setCell(cellIndex, new MonsterCell("Monster Cell " + cellIndex, stationedMonster));
		}
	}

    private void setCardsByRarity() {
		ArrayList<Card> expandedList = new ArrayList<>();
        for (int i = 0; i < originalCards.size(); i++) {
			Card card = originalCards.get(i);
            int rarity = card.getRarity(); 

            for (int j = 0; j < rarity; j++) {
                expandedList.add(card);
            }
        }
        originalCards = expandedList; 
    }

    public static void reloadCards() {
		cards = new ArrayList<>(originalCards);
        Collections.shuffle(cards);
    }

    public static Card drawCard() {
        if (cards.isEmpty()) 
            reloadCards();
        return cards.remove(0);
    }

    public void moveMonster(Monster currentMonster, int roll, Monster opponentMonster) throws InvalidMoveException {
    }
	
    private void updateMonsterPositions(Monster player, Monster opponent) { 
		
        for (int i = 0; i < Constants.BOARD_ROWS; i++) {
            for (int j = 0; j < Constants.BOARD_COLS; j++) {
                boardCells[i][j].setMonster(null);
            }
        }

        getCell(player.getPosition()).setMonster(player);
        getCell(opponent.getPosition()).setMonster(opponent);
    }

}
