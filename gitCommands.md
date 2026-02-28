## Quick Github Guide for Team 203
We're mainly using the [Github Desktop app](https://desktop.github.com/) 3ashan as-hal w it prevents us from accidentally uploading broken Eclipse files.

**Index:**
1. [First-Time Setup](#1-first-time-setup)
2. [The Daily Start (Pulling)](#2-the-daily-start-pulling)
3. [The Daily Finish (Saving)](#3-the-daily-finish-saving)
4. [Merge Conflicts](#4-merge-conflicts)
5. [Emergency Abort](#5-emergency-abort)
6. [Interrupted Save (Stashing)](#6-interrupted-save-stashing)
7. [Rollback a Bug](#7-rollback-a-bug)
8. [Safe Sandbox (Branches)](#8-safe-sandbox-branches)

### 1. First-Time Setup
- Nazzelo GitHub Desktop w e3melo login.
- Go to `File > Clone repository...`
- Click the **URL** tab and paste our link (e.g., `https://github.com/ishatemsalem/CSEN401-Team203.git`).
- Choose your exact Eclipse workspace in the Local Path (like `C:\Users\ishatemsalem\eclipse-workspace\CSEN401-Team203`). Clone it.
- To link it in Eclipse: `File > Import > General > Existing Projects into Workspace`, select the folder, Finish.

### 2. The Daily Start (Pulling)
Awal ma tefta7 el PC te3mel keda. **Never write code without checking this first.**
- Open GitHub Desktop and click **Fetch origin** on top.
- Law 7ad mel team rafa3 code gded, it changes to **Pull origin**. Click it.
- **CRITICAL:** Open Eclipse, click our project folder, w edghat **F5** (Refresh). Law neseit, Eclipse will literally overwrite their new code with your cached files

### 3. The Daily Finish (Saving)
khalast shoghl? time to save.
- Look at the **Changes** panel on the left in the app. Green = added lines, Red = deleted.
- Check the boxes for the files you actually finished (masalan `Dasher.java`). Uncheck any half-baked stuff.
- Ekteb summary wade7 in the bottom left (like *"Implemented Dasher momentum"*).
- Click **Commit to main**, then **Push origin** at the top. Your unfinished stuff stays safe locally on your PC for tomorrow.

### 4. Merge Conflicts
Law enta w 7ad tany edited the exact same file at the same time. App will yell at you: "Resolve conflicts before merging."
- Open Eclipse, hit **F5**.
- Open the broken file (`Board.java`). Hatla2y weird symbols malhash lazma `<<<<<<< HEAD` w `=======`.
- Manually delete the symbols w zabbat el code sa7 for Milestone 1 requirements.
- Save, go back to GitHub Desktop, hit **Continue Merge**, and push.

### 5. Emergency Abort
Law 3akeit el denya w want to go back to the last working server version.
- Make sure you haven't clicked "Commit".
- Open terminal/CMD w edkhol 3ala the project folder: `cd C:\Users\YourName\eclipse-workspace\CSEN401-Team203`
- Ekteb `git restore .` (This absolute command wipes all uncommitted local changes).
- Eclipse -> F5. Everything is clean again.

### 6. Interrupted Save (Stashing)
You're halfway through writing code, bas someone pushed an urgent update.
- Click **Pull origin**. The app will ask what to do with your unfinished files.
- Choose **"Stash my changes and continue."**
- Github hides your code in a vault, downloads the team's update, then you click **View Stashed Changes > Restore** (bottom left) to drop your unfinished code back on top.

### 7. Rollback a Bug
Pushed a bug that broke the game for everyone?
- Go to the **History** tab in GitHub Desktop.
- Right-click your bad commit w ekhter **Revert commit**.
- It does the mathematical opposite of your mistake (deletes what you added, adds what you deleted). Hit **Push origin** to send the fix.

### 8. Safe Sandbox (Branches)
Want to test a crazy feature bas kheyef it crashes `main`?
- Click **Current Branch: main** at the top center -> **New Branch**. Name it something descriptive (e.g., `dataloader-test`).
- Publish branch. Enta keda in a parallel universe. A3mel el enta 3aizo.
- When it's 100% bug-free, click **Create Pull Request** to safely merge it into our main game.
