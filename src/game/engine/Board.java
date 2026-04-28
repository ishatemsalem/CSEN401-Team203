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

		/* */
		setCardsByRarity();
        reloadCards();
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
    

	private boolean contains(int[] arr, int value) { //helper method to check if an index belongs to a certain type of cell
		if (arr == null) {
			return false;
		}
		for(int i=0; i<arr.length; i++) {
			if (arr[i] == value) {
				return true;
			}
		}
		return false;
	}

	public void initializeBoard(ArrayList<Cell> specialCells) {
		if (specialCells == null) {
			throw new IllegalArgumentException("specialCells list cannot be null");
		}

		// Separate the CSV-loaded cells by type
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

		int doorIndex = 0;
		int conveyorIndex = 0;
		int sockIndex = 0;
		int monsterIndex = 0;

		Cell cellToPlace = null;

		for (int i = 0; i < Constants.BOARD_SIZE; i++) {

			if (i % 2 == 0) {

				if (doorIndex < doorCells.size()) {
					cellToPlace = doorCells.get(doorIndex++);
				} else {
					cellToPlace = new Cell("Normal Rest Cell " + i);
				}

			} else {

				if (contains(Constants.CARD_CELL_INDICES, i)) {

					cellToPlace = new Cell("Card Cell " + i);

				} else if (contains(Constants.CONVEYOR_CELL_INDICES, i)) {

					if (conveyorIndex < conveyorBelts.size()) {
						cellToPlace = conveyorBelts.get(conveyorIndex++);
					} else {
						cellToPlace = new Cell("Normal Rest Cell " + i);
					}

				} else if (contains(Constants.SOCK_CELL_INDICES, i)) {

					if (sockIndex < contaminationSocks.size()) {
						cellToPlace = contaminationSocks.get(sockIndex++);
					} else {
						cellToPlace = new Cell("Normal Rest Cell " + i);
					}

				} else if (contains(Constants.MONSTER_CELL_INDICES, i)) {

					if (monsterIndex < stationedMonsters.size()) {
						Monster stationedMonster = stationedMonsters.get(monsterIndex++);
						stationedMonster.setPosition(i);
						cellToPlace = new MonsterCell(
							stationedMonster.getName(),
							stationedMonster
						);
					} else {
						cellToPlace = new Cell("Normal Rest Cell" + i);
					}

				} else {
					cellToPlace = new Cell("Normal Rest Cell" + i);
				}
			}

			setCell(i, cellToPlace);
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
        int originalPosition = currentMonster.getPosition();
        
        currentMonster.move(roll);

        Cell landedCell = getCell(currentMonster.getPosition());
        if (landedCell != null) {
            landedCell.onLand(currentMonster, opponentMonster);
        }

// exception if collission
        if (currentMonster.getPosition() == opponentMonster.getPosition() && currentMonster.getPosition() != 0) {
            currentMonster.setPosition(originalPosition);
            throw new InvalidMoveException("Invalid move: Cannot land on the opponent's cell.");
        }

        if (currentMonster.isConfused()) {
            currentMonster.decrementConfusion();
        }
        if (opponentMonster.isConfused()) {
            opponentMonster.decrementConfusion();
        }

        updateMonsterPositions(currentMonster, opponentMonster);
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
