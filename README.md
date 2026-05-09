# زملائي في Team 203! علشان نكون كلنا في نفس الصفحة، اليكم التوجيهات القياسية
- No external libraries are allowed. Akherna java.* (dw tho its still possible to make mindblowing UI for milestone 3)
- AI is allowed, but all chats must be saved and submitted in a Team203logs.zip. If AI is detected for a part and no logs are present severe deductions will be done. fa dont delete anything and dont say anything you wouldnt want them to see

## Software Stack
Milestones 1 & 2:
- JDK 21 (STRICT)
- Eclipse IDE
- Github
- Discord
  
Milestone 3:
- Blender 4.2 LTS + "SS Vantage Suite: Sprite Sheet" ;]
- Krita Any version
- Audacity Any version, strictly exporting to .wav
- TexturePacker

## Folder Structure so far:
- wala 7aga :D

## Milestone 1:
> Deadline: March 12, 2026 @ 11:59 PM.

The src directory must contain only the following exact packages:
- game.engine
- game.engine.cards
- game.engine.cells
- game.engine.monsters
- game.engine.dataloader
- game.engine.exceptions
- game.engine.interfaces
- game.tests

### Responsibilities:

purple (Ja):
- enums
- constants classes
- game class (under game setup)
- exceptions class (all subclasses: InvalidMoveException
                                    InvalidTurnException
                                    OutofEnergyException)

green (Is):
- CanisterModifier Interface
- Card class (all subclasses: SwapperCard
                              EnergyStealCard
                              StartoverCard
                              ConfusionCard
                              Shieldcard)

blue (Ju):
- Monster class (all subclasses:  Dasher
                                  Dynamo
                                  Multitasker
                                  Schemer)
- DataLoader class (under game setup)

red (Ro):
- Cell class
- doorcell
- monstercell
- cardcell
- transportcell
- ConveyorBelt
- ContaminationSock

### Milestone 2: 

### Milestome 3: 

Person 1 - blue - Jana

“Player must be able to perform the following actions”
Choose whether to activate powerup before rolling
Roll dice to move monster

“Player should keep track of the following throughout the game”
Current turn
Current player/opponent
Dice result
Drawn card display
Freeze indication

“Must be shown and updated for each card”
Card name/effect
Card effect indication

“Must be shown for any invalid action”
Exception popups
Invalid action messages
Prevent crashing
Closing popup shouldn’t close game


Person 2 - pink - Judy

“Game board with all 100 cells...”
100-cell board
Correct positions/types
Cell index numbers
Different cell visuals
Door energy
Initial monster placement
Card setup

“Must be shown and updated on the board”
Cell effects
Activated/exhausted doors
Monster cell identities
Energy changes
Shield blocking indication
Updating monster positions


Person 3 - green - Jana

“Must be displayed for each monster”
Name
Original role
Current role
Type
Energy
Position
Status effects

“Must be shown and updated whenever a player ends a turn”
Updated positions
Updated energy
Status durations
Confusion role visuals

"Player must be able to perform the following actions"
Distinguish between monster types

This person handles:

Person 4 - yellow - Islam

“Must be displayed whenever the player starts the game”
Start screen
Side selection
Start game
Instructions

“Must be shown whenever a player wins”
Game won screen
Winner name/role
Final energies
Return to start window

### OOP guidelines:
- All class instance variables must be explicitly declared as private.
- Any attribute defined as a constant must be declared as public static final.
- Accessors/mutators: Getters and setters must follow Java camelCase convention eg. getEnergy(). If instance var is boolean, getter must begin with "is" eg. isAlive()
- Data load: Add DataLoader to parse cards.csv, cells.csv, and monsters.csv. These files must be in project root, not inside src folder
- Sorting mechanism: Monster class must implement Comparable interface to sort instances based on board position
- Exception handling: Establish custom exception classes, including InvalidCSVFormat to catch bad data rows
