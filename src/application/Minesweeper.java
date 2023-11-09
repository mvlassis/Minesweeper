package application;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Random;

/**
* Minesweeper is a class that fully represents and simulates a Minesweeper
* game. The main element of the class is a square grid (minefield) of a 
* specific size (gridSize). Each cell in the grid can be empty, have a mine,
* or have a hypermine. Additionally, each cell has a different reveal status
* whether it has been revealed or not. Note that each new game has random
* placements for the mines, and all cells start as not revealed (hidden).
* Each cell also has a different value whether it has been revealed due to
* a hypermine. Finally, a Minesweeper object has a current game status that
* can change depending on the actions of the player.
* 
* @author      Manos Vlassis
* @version     1.0
* @since       1.0
*/
public class Minesweeper {
	public static final int EMPTY = 0;
    public static final int MINE = -1;
    public static final int HYPERMINE = -2;

    public static final int HIDDEN = 0;
    public static final int REVEALED = 1;
    
    public static final int UNMARKED = 0;
    public static final int MARKED = 1;
    
    public static final int NOT_HYPERMINED = 0;
    public static final int HYPERMINED = 1;
    
    public static final int GAME_PLAYING = 0;
    public static final int GAME_LOST = 1;
    public static final int GAME_WON = 2;

    private int[][] minefield;
    private int[][] revealStatus;
    private int[][] markStatus;
    private int[][] hyperminedStatus;
    
    private int revealedCells;
    private int markedMines;
    private int totalMines;
    private int gridSize;
    private int totalClicks;
    private int hasHypermine;
    
    private int gameStatus;

    /**
     * Constructor for the class. Initializes a new Minesweeper object
     * and all its fields. All cells start as HIDDEN, UNMARKED, and 
     * NOT_HYPERMINED.
     * 
     * @param  gridSize		the number of rows and columns of the minefield
     * @param  mines		total number of mines to include
     * @param  time	        total available time for the game
     * @param  hasHypermine 1 if the game has a hypermine, 0 if it doesn't
     * @return              a Minesweeper object, fully initialized
     */
    public Minesweeper(int gridSize, int mines, int time, int hasHypermine) {
        this.gridSize = gridSize;
        this.hasHypermine = hasHypermine;
        totalMines = mines;
        markedMines = 0;
        revealedCells = 0;
        totalClicks = 0;
        gameStatus = GAME_PLAYING;

        minefield = new int[gridSize][gridSize];
        revealStatus = new int[gridSize][gridSize];
        markStatus = new int[gridSize][gridSize];
        hyperminedStatus = new int[gridSize][gridSize];

        for (int[] row: revealStatus) {
            Arrays.fill(row, HIDDEN);
        }
        
        for (int[] row: markStatus) {
        	Arrays.fill(row, UNMARKED);
        }
        
        for (int[] row: hyperminedStatus) {
        	Arrays.fill(row, NOT_HYPERMINED);
        }

        Random random = new Random();
        int placedMines = 0;
        int hyperMinePosition = 0;
        if (hasHypermine == 1) {
            hyperMinePosition = random.nextInt(mines);
        }
        while (placedMines < mines) {
            int row = random.nextInt(gridSize);
            int col = random.nextInt(gridSize);
            if (minefield[row][col] != MINE && minefield[row][col] != HYPERMINE) {
                if (hasHypermine == 1 && hyperMinePosition == placedMines) {
                    minefield[row][col] = HYPERMINE;
                }
                else {
                    minefield[row][col] = MINE;
                }
                placedMines++;
            }
        }
        
        for (int i = 0; i < gridSize; i++) {
        	for (int j = 0; j < gridSize; j++) {
        		if (minefield[i][j] == MINE || minefield[i][j] == HYPERMINE) {
        			continue;
        		}
        		else {
        			int nearby_mines = 0;
            		if (i-1 >= 0 && j-1 >= 0) {
            			if (minefield[i-1][j-1] == MINE || minefield[i-1][j-1] == HYPERMINE) nearby_mines++;
            		}
            		if (i-1 >= 0) {
            			if (minefield[i-1][j] == MINE || minefield[i-1][j] == HYPERMINE) nearby_mines++;
            		}
            		if (i-1 >= 0 && j+1 < gridSize) {
            			if (minefield[i-1][j+1] == MINE || minefield[i-1][j+1] == HYPERMINE) nearby_mines++;
            		}
            		if (j-1 >= 0) {
            			if (minefield[i][j-1] == MINE || minefield[i][j-1] == HYPERMINE) nearby_mines++;
            		}
            		if (j+1 < gridSize) {
            			if (minefield[i][j+1] == MINE || minefield[i][j+1] == HYPERMINE) nearby_mines++;
            		}
            		if (i+1 < gridSize && j-1 >= 0) {
            			if (minefield[i+1][j-1] == MINE || minefield[i+1][j-1] == HYPERMINE) nearby_mines++;
            		}
            		if (i+1 < gridSize) {
            			if (minefield[i+1][j] == MINE || minefield[i+1][j] == HYPERMINE) nearby_mines++;
            		}
            		if (i+1 < gridSize && j+1 < gridSize) {
            			if (minefield[i+1][j+1] == MINE || minefield[i+1][j+1] == HYPERMINE) nearby_mines++;
            		}
            		minefield[i][j] = nearby_mines;
        		} 		
        	}
        }
    }

    /**
    * Simulates left clicking a specific cell in the game. This method
    * increases the total click count, then calls the reveal() method
    * with the same arguments.
    *
    * @param  i	 the row of the cell, starting from 0
    * @param  j	 the column of the cell, starting from 0 
    */
    public void click(int i, int j) {
    	totalClicks++;
    	reveal(i, j);
    }  
    
    /**
     * Simulates left clicking a specific cell in the game. This method
     * marks a hidden cell if it is unmarked, or unmarks a cell if it is
     * marked. There cannot be more marked cell than the total number of
     * mines in the game.
     * <p>
     * If a cell that has a hypermine is marked within the first 4 clicks,
     * then revealHyperMine() is called on all the cell in the same row or
     * the same column.
     *
     * @param  i	 the row of the cell that will be revealed, starting from 0
     * @param  j	 the column of the cell that will be revealed, starting from 0 
     */
    public void mark(int i, int j) {
    	if (revealStatus[i][j] == HIDDEN && markStatus[i][j] == UNMARKED && markedMines < totalMines) {
    		if (minefield[i][j] == HYPERMINE && totalClicks <= 4) {
    			for (int z = 0; z < i; z++) {
    				revealHyperMine(z, j);
    			}
    			for (int z = i; z < gridSize; z++) {
    				revealHyperMine(z, j);			
    			}
    			for (int z = 0; z < j; z++) {
    				revealHyperMine(i, z);
    			}
    			for (int z = j; z < gridSize; z++) {
    				revealHyperMine(i, z);
    			}
    		}
    		else {
    			markStatus[i][j] = MARKED;
    			markedMines++;
    		}
    	}
    	else if (revealStatus[i][j] == HIDDEN && markStatus[i][j] == MARKED) {
    		markStatus[i][j] = UNMARKED;
    		markedMines--;
    	}
    }
    
   
    /**
     * Ends the game by revealing all mines, then marks the game as lost.
     *
     */
    public void revealSolution() {
    	for (int i = 0; i < gridSize; i++) {
    		for (int j = 0; j < gridSize; j++) {
    			if (minefield[i][j] == MINE || minefield[i][j] == HYPERMINE) {
    				revealStatus[i][j] = REVEALED;
    			}		
    		}
    	}
    	gameStatus = GAME_LOST;
    }
    
    /**
     * Opens a file with the given name for writing, then writes the position
     * of all mines in the game. The file has one line for each mine. Each
     * line has 3 numbers, one for the mine row, one for the mine column, and
     * 1 or 0 whether the mine is a hypermine or not.
     *
     * @param  name the file name on which all information will be written
     */
    public void saveMinefield(String name) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(name));
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (minefield[i][j] == MINE || minefield[i][j] == HYPERMINE) {
                        bufferedWriter.write(i + "," + j + ",");
                        if (minefield[i][j] == MINE) {
                            bufferedWriter.write(0 + "\n");
                        }
                        else {
                            bufferedWriter.write(1 + "\n");
                        }
                    }
                }
            }

            bufferedWriter.close();
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Appends the state of the game to a "savedgames.txt" on the medialab
     * folder. Includes information on the total number of mines, the total
     * number of clicks, the time left, and whether the game was lost or won.
     *
     * @param timeLeft the time left on the clock when the method was called	   
     */
    public void saveGame(String timeLeft) {
		try {
			FileWriter fileWriter = new FileWriter("medialab/savedgames.txt", true); 
			String save = String.valueOf(totalMines) + " " + totalClicks + " " + timeLeft + " " + gameStatus + "\n";
            fileWriter.write(save);
            fileWriter.close();
		    	    
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Returns the size of the minefield grid. We assume the field has
     * the same row and column length.
     *
     * @return the grid size (must be greater than 0)
     */
    public int getGridSize() {
    	return gridSize;
    }
    
    /**
     * Returns the minefield type of a specific cell.
     *
     * @param  i the row of the cell, starting from 0
     * @param  j the column of the cell, starting from 0 
     * @return   the type of the cell
     */
    public int getCell(int i, int j) {
    	return minefield[i][j];
    }
    
    /**
     * Returns the reveal status of a specific cell.
     *
     * @param  i the row of the cell, starting from 0
     * @param  j the column of the cell, starting from 0 
     * @return   the reveal status of the cell
     */
    public int getRevealStatus(int i, int j) {
    	return revealStatus[i][j];
    }
    
    /**
     * Returns the mark status of a specific cell.
     *
     * @param  i the row of the cell, starting from 0
     * @param  j the column of the cell, starting from 0 
     * @return   the mark status of the cell
     */
    public int getMarkStatus(int i, int j) {
    	return markStatus[i][j];
    }
    
    /**
     * Returns the current number of marked mines in the game.
     *
     * @return the current number of marked mines
     */
    public int getMarkedMines() {
    	return markedMines;
    }
    
    /**
     * Returns the total number of mines in the game.
     *
     * @return the total number of mines
     */
    public int getTotalMines() {
    	return totalMines;
    }
    
    /**
     * Returns whether the specific game has a hypermine. Returns 1 if it has,
     * or 0 if it doesn't have one.
     *
     * @return 1 if the game has a hypermine, 0 if it doesn't
     */
    public int hasHypermine() {
    	return hasHypermine;
    }
    
    /**
     * Returns whether a specific cell has been revealed due to a hypermine.
     *
     * @param  i the row of the cell, starting from 0
     * @param  j the column of the cell, starting from 0 
     * @param    the hypermined status of the cell
     */
    public int getHyperminedStatus(int i, int j) {
    	return hyperminedStatus[i][j];
    }
    
    /**
     * Returns the status of the game (GAME_WON, GAME_LOST, or GAME_PLAYING)
     *
     * @return the current status of the game
     */
    public int getGameStatus() {
    	return gameStatus;
    }
    
    /**
     * Marks the game as lost (usually called when the time runs out)
     *
     */
    public void setLost() {
    	gameStatus = GAME_LOST;
    }
    
    private void revealHyperMine(int i, int j) {
    	if (revealStatus[i][j] == HIDDEN) {
    		revealStatus[i][j] = REVEALED;
    		hyperminedStatus[i][j] = HYPERMINED;
    		if (minefield[i][j] >= EMPTY) {
    			
    			revealedCells++;
    			if (revealedCells >= gridSize * gridSize - totalMines && gameStatus == GAME_PLAYING) {
    				gameStatus = GAME_WON;
    			}
    		}
    	}
    	if (markStatus[i][j] == MARKED) {
			markStatus[i][j] = UNMARKED;
			markedMines--;
		}
    }
    
    private void reveal(int i, int j) {   	
    	if (revealStatus[i][j] == HIDDEN) {		
    		revealStatus[i][j] = REVEALED;
    		if (minefield[i][j] == MINE || minefield[i][j] == HYPERMINE && gameStatus == GAME_PLAYING) {
    			gameStatus = GAME_LOST;
    		}
    		if (markStatus[i][j] == MARKED) {
    			markStatus[i][j] = UNMARKED;
    			markedMines--;
    		}
    		revealedCells++;
    		if (revealedCells >= gridSize * gridSize - totalMines && gameStatus == GAME_PLAYING) {
    			gameStatus = GAME_WON;
    		}
    		if (minefield[i][j] == EMPTY) {
    			if (i-1 >= 0 && j-1 >= 0) {
    				if (revealStatus[i-1][j-1] == HIDDEN) reveal(i-1, j-1);
    			}
    			if (i-1 >= 0) {
    				if (revealStatus[i-1][j] == HIDDEN) reveal(i-1, j);
    			}
    			if (i-1 >= 0 && j+1 < gridSize) {
    				if (revealStatus[i-1][j+1] == HIDDEN) reveal(i-1, j+1);
    			}
    			if (j-1 >= 0) {
    				if (revealStatus[i][j-1] == HIDDEN) reveal(i, j-1);
    			}
    			if (j+1 < gridSize) {
    				if (revealStatus[i][j+1] == HIDDEN) reveal(i, j+1);
    			}
    			if (i+1 < gridSize && j-1 >= 0) {
    				if (revealStatus[i+1][j-1] == HIDDEN) reveal(i+1, j-1);
    			}
    			if (i+1 < gridSize) {
    				if (revealStatus[i+1][j] == HIDDEN) reveal(i+1, j);
    			}
    			if (i+1 < gridSize && j+1 < gridSize) {
    				if (revealStatus[i+1][j+1] == HIDDEN) reveal(i+1, j+1);
    			}
    		}
    	}
    }
}
