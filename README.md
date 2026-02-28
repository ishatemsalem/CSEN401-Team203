# زميلاتي في Team 203! علشان نكون كلنا في نفس الصفحة، اليكم التوجيهات القياسية
- No external libraries are allowed. Akherna java.* (dw tho its still possible to make mindblowing UI for milestone 3)
- AI is allowed, but all chats must be saved and submitted in a Team203logs.zip. If AI is detected for a part and no logs are present severe deductions will be done. fa dont delete anything and dont say anything you wouldnt want them to see

## Software Stack
Milestones 1 & 2:
- JDK 21 (STRICT)
- Eclipse IDE
- Github
- Discord
  
Milestone 3:
- Blender 4.6 LTS + "SS Vantage Suite: Sprite Sheet" ;]
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

OOP guidelines:
- All class instance variables must be explicitly declared as private.
- Any attribute defined as a constant must be declared as public static final.
- Accessors/mutators: Getters and setters must follow Java camelCase convention eg. getEnergy(). If instance var is boolean, getter must begin with "is" eg. isAlive()
- Data load: Add DataLoader to parse cards.csv, cells.csv, and monsters.csv. These files must be in project root, not inside src folder
- Sorting mechanism: Monster class must implement Comparable interface to sort instances based on board position
- Exception handling: Establish custom exception classes, including InvalidCSVFormat to catch bad data rows
