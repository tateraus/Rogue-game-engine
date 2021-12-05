package rogueProject;
/**
 * A simulation program for simulating a game with a main menu, where users
 * can issue particular commands to configure the game, a very simple game world,
 * where the user can control their player moving around and trigger monster encounters.
 * When a player enters a location on the map where a monster exists, the game enters a
 * battle loop
 * @author: Yingxue Chen, yingxuec1@student.unimelb.edu.au, 1210613
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GameEngine {
    // instance variables
    private Player player;
    private Monster monster;
    private World myCurrentGameWorld;
    private Map myGameWorldMap;
    private final String[] commands = {"help", "player", "monster", "start", "exit", "save", "load"};
    public static final String PLAYER_FILE= "player.dat";
    private int damageIncrement;


    public static void main(String[] args) {
        // Creates an instance of the game engine.
        GameEngine gameEngine = new GameEngine();
        // initiate an object from the Scanner class in the main so that the whole program can share
        Scanner scan = new Scanner(System.in);
        // Runs the main game loop.
        gameEngine.runGameLoop(scan);
    }

    /**
     *  Logic for running the main game loop.
     */
    private void runGameLoop(Scanner scan) {
        // display the main menu at the beginning of the game loop
        displayMainMenu();
        getUserCommands(scan);
    }

    /**
     * To get user's commands and execute each commands according to its designated functions
     * @param scan
     */
    private void getUserCommands(Scanner scan) {
        System.out.print("> ");
        String userEntry = scan.nextLine().toLowerCase();
        while (userEntry.equals("")) {
            userEntry = scan.nextLine().toLowerCase();
        }
        String[] userEntryArray = userEntry.split(" ");

        switch (userEntryArray[0]) {
            case "help":
                displayHelpText();
                getUserCommands(scan);
                break;
            case "commands":
                displayCommandsText();
                getUserCommands(scan);
                break;
            case "player":
                // create a new player if not exits, otherwise displays current player information
                if (player == null) {
                    playerCreation(scan);
                } else {
                    displayExistPlayer();
                }
                scan.nextLine();
                runGameLoop(scan);
                break;
            case "monster":
                monsterCreation(scan);
                runGameLoop(scan);
                break;
            case "start":
                if (userEntryArray.length == 1) { // which means the user only entered 'start'
                    startDefaultGame(scan);
                }
                else { // gameType is file-structured
                    try {
                        String fileName = userEntryArray[1];
                        damageIncrement = 0;
                        startFileGame(fileName, scan);
                    } catch (FileNotFoundException e) {
                        try {
                            throw new GameLevelNotFoundException("Map not found.");
                        } catch (GameLevelNotFoundException eNew) {
                            System.out.println(eNew.getMessage());
                            System.out.println("\n(Press enter key to return to main menu)");
                            scan.nextLine();
                        }
                    } catch (IOException e) {
                        System.out.println("An error occurred while loading the file.");
                    }
                }
                runGameLoop(scan);
                break;
            case "exit":
                System.out.println("Thank you for playing Rogue!");
                break;
            case "save":
                savePlayer();
                getUserCommands(scan);
                break;
            case "load":
                loadPlayer();
                getUserCommands(scan);
                break;
            default:
                System.out.println();
                runGameLoop(scan);
                break;
        }
    }

    /**
     * To start a default game with existing player and monster, if either player or monster doesn't exist, it will
     * display prompt message to the user; If both player and monster exist, it will generate a default game map and
     * start a default game world with all the player, monster and map information.
     * @param scan
     */
    private void startDefaultGame(Scanner scan) {
        if (player == null) {
            displayNoPlayerText(scan);
        } else if (monster == null) {
            displayNoMonsterText(scan);
        } else {
            // heal both player and monster before start the game
            myGameWorldMap = new Map();
            gainDefaultMap();
            myCurrentGameWorld = new World(player, monster, myGameWorldMap);
            healPlayerMonster();
            defaultGameWorld(scan);
        }
    }

    /**
     * To generate a default map for the default game using ArrayList structure
     */
    private void gainDefaultMap() {
        for (int i = 0; i < myGameWorldMap.getMapHeight(); i++) {
            ArrayList<Entity> innerList = new ArrayList<>();
            for (int j = 0; j < myGameWorldMap.getMapWidth(); j++) {
                innerList.add(new Entity("."));
            }
            myGameWorldMap.setOriginalMapList(innerList);
        }
    }

    /**
     * To generate a default game world with preset default map and run the default game with given user input.
     * @param scan
     */
    private void defaultGameWorld(Scanner scan) {
        // to insert player and monster onto the default map which only has dots present
        myCurrentGameWorld.insertPlayerMonsterDefaultGame();
        printMap(myGameWorldMap.getOriginalMapList());
        System.out.print("\n> ");
        String move = scan.nextLine().toLowerCase();

        while (move != null) {
            if (move.equalsIgnoreCase("home")) {
                displayReturnHome(scan);
                break;
            } else {
                // each time when the player move ,we generate a default map with only dots
                // and then set player and monster onto the map via updating the arrayList
                myGameWorldMap = new Map();
                gainDefaultMap();
                myCurrentGameWorld.setMyCurrentMap(myGameWorldMap); // to make sure the current map is a default map
                myCurrentGameWorld.playerMoveUpdateXy(move); // update player coordinates and then set into the new map
                boolean meet = myCurrentGameWorld.insertPlayerMonsterDefaultGame();
                if (meet) {
                    String game = "default";
                    battleStart(monster, game, scan);
                    scan.nextLine();
                    break;
                } else {
                    printMap(myGameWorldMap.getOriginalMapList());
                    System.out.print("\n> ");
                    move = scan.nextLine().toLowerCase();
                }
            }
        }
    }

    /**
     * To display the line under the 'Rogue' title with updated player and monster's information via toString
     * method in respective classes
     */
    private void displayPlayerMonsterStatus() {
        if (player == null && monster == null) {
            System.out.println("Player: [None]  | Monster: [None]");
        } else if (player == null) {
            System.out.println("Player: [None]  | Monster: " + monster);
        } else if (monster == null) {
            System.out.println("Player: " + player + "  | Monster: [None]");
        } else {
            System.out.println("Player: " + player + "  | Monster: " + monster);
        }
    }

    /**
     * To display the exiting player's information
     */
    private void displayExistPlayer() {
        System.out.println(player.getName() + " (Lv. " + player.getLevel() + ")");
        System.out.println("Damage: " + player.getDamage());
        System.out.println("Health: " + player.getCurHealth()
                + "/" + player.getMaxHealth());
        System.out.println("(Press enter key to return to main menu)");
    }

    /**
     * To display the main menu of the gain with the updated player and monster's status
     */
    private void displayMainMenu() {
        displayTitleText();
        displayPlayerMonsterStatus();
        System.out.println();
        displayPromptInfoToUser();
    }

    /**
     *  To display the title "Rogue" text.
     */
    public void displayTitleText() {

        String titleText = " ____                        \n" +
                "|  _ \\ ___   __ _ _   _  ___ \n" +
                "| |_) / _ \\ / _` | | | |/ _ \\\n" +
                "|  _ < (_) | (_| | |_| |  __/\n" +
                "|_| \\_\\___/ \\__, |\\__,_|\\___|\n" +
                "COMP90041   |___/ Assignment ";

        System.out.println(titleText);
        System.out.println();
    }

    /**
     * To display prompt text messages to the user
     */
    private void displayPromptInfoToUser() {
        System.out.println("Please enter a command to continue.");
        System.out.println("Type 'help' to learn how to get started.\n");
    }

    /**
     * To display the helping text to the user
     */
    private void displayHelpText() {
        System.out.println("Type 'commands' to list all available commands\n" + "Type 'start' to start a new game\n"
                + "Create a character, battle monsters, and find treasure!");
        System.out.println();
    }

    /**
     * To display a list of available commands.
     */
    private void displayCommandsText() {
        for (String command: commands) {
            System.out.println(command);
        }
        System.out.println();
    }

    /**
     * To display the prompt text message that a monster hasn't been created to the user
     */
    private void displayNoMonsterText(Scanner scan) {
        System.out.println("No monster found, please create a monster with 'monster' first.\n");
        System.out.println("(Press enter key to return to main menu)");
        scan.nextLine();
    }

    /**
     * To display the prompt text massage that the player hasn't been created to the user
     */
    private void displayNoPlayerText(Scanner scan) {
        System.out.println("No player found, please create a player with 'player' first.\n");
        System.out.println("(Press enter key to return to main menu)");
        scan.nextLine();
    }

    /**
     * To create a new monster after the "monster" command given by user
     */
    private void monsterCreation(Scanner scan) {
        System.out.print("Monster name: ");
        String monsterName = scan.nextLine();
        System.out.print("Monster health: ");
        int monsterMaxHealth = Integer.parseInt(scan.nextLine());
        System.out.print("Monster damage: ");
        int monsterDamage = Integer.parseInt(scan.nextLine());
        monster = new Monster(monsterName, monsterMaxHealth, monsterDamage);
        System.out.println("Monster '" + monsterName + "' created.\n");
        System.out.println("(Press enter key to return to main menu)");
        scan.nextLine();
    }

    /**
     * To load previously saved player into the game, if no player saved before return no data found
     */
    private void loadPlayer() {
        try {
            // To read the file which saved the player's name and level
            BufferedReader inputReader = new BufferedReader(new FileReader(PLAYER_FILE));
            String line = inputReader.readLine();
            if (line == null) {
                System.out.println("No player data found");
                System.out.print("\n> ");
            } else {
                // split the read-in line into an array to access different type of player's information
                String[] lineWord = line.split(" ");
                int numOfWord = 2;
                try {
                    if (lineWord.length == numOfWord) {
                        String playerName = lineWord[0];
                        int playerLevel = Integer.parseInt(lineWord[1]);
                        int playerX = 1; int playerY = 1;
                        player = new Player(playerName, playerLevel, playerX, playerY);
                        System.out.println("Player data loaded.");
                        System.out.println();
                        return; }
                } catch(NumberFormatException e) {
                    System.out.println("Player level format not right");
                }
                inputReader.close(); }
        }
        catch(FileNotFoundException e) {
            try {
                // if a FileNotFoundException is found then it will throws a new GameLevelNotFoundException
                throw new GameLevelNotFoundException("Map not found.");
            }
            catch (GameLevelNotFoundException eNew) {
                System.out.println(eNew.getMessage());
            }
        }
        catch (IOException e) {
            System.out.println("An error occurred while loading the file.");
        }
    }

    /**
     * To save a created player's name and level into a file called "player.dat", any previous data
     * will be overwritten
     */
    private void savePlayer() {
        if (player != null) {
            PrintWriter outputStream = null;
            try { // FileOutputStream will throws a FileNotFoundException if the file can't be opened or found
                outputStream = new PrintWriter(new FileOutputStream(PLAYER_FILE));
            }
            catch (FileNotFoundException e) {
                System.out.println("Error opening the file");
                System.exit(0);
            }
            outputStream.print(player.getName() + " " + player.getLevel());
            System.out.println("Player data saved.");
            outputStream.close();
        } else {
            System.out.println("No player data to save.");
        }
        System.out.println();
    }

    /**
     * To create a new player in the default game world with level set at 1, and name determined by the user
     */
    private void playerCreation(Scanner scan) {
        System.out.println("What is your character's name?");
        String playerName = scan.nextLine();
        player = new Player(playerName);
        System.out.println("Player '" + playerName + "' created.");
        System.out.println();
        System.out.println("(Press enter key to return to main menu)");
    }

    /**
     * To extract the game information from the given file and start a file based game. If no player exist,
     * it will display a prompt message back to user. The exceptions thrown by this function will be handled
     * by the calling function getUserCommands.
     * @param fileName
     * @param scan
     * @throws FileNotFoundException, IOException
     */
    private void startFileGame(String fileName, Scanner scan)
            throws FileNotFoundException, IOException {
        ArrayList<String> fileWorld = new ArrayList<>();
        if (player != null) {
            fileName = fileName + ".dat";
            gainFileInput(fileWorld, fileName);
            decomposeFileData(fileWorld);
            fileGameWorld(scan);
            player.setDamage(player.getDamage() + damageIncrement);
        } else {
            displayNoPlayerText(scan);
        }
    }

    /**
     * To read the file contents into an ArrayList<String> using BufferedReader
     * @param fileWorld
     * @throws FileNotFoundException, IOException
     */
    private void gainFileInput (ArrayList<String> fileWorld, String fileName)
            throws FileNotFoundException, IOException {
        BufferedReader inputStream =
                new BufferedReader(new FileReader(fileName));
        String line = inputStream.readLine();
        while (line != null) {
            fileWorld.add(line);
            line = inputStream.readLine();
        }
        inputStream.close();
    }

    /**
     * To decompose the file data and save into different data structures and into different classes and also to
     * create a new game world with given data in the file
     * @param fileWorld
     */
    private void decomposeFileData(ArrayList<String> fileWorld) {
        ArrayList<Monster> monstersList = new ArrayList<>(); // temporarily save the monster and item data
        ArrayList<Item> itemsList = new ArrayList<>();

        try { // to catch the possible NumberFormatException from Integer.parseInt
            boolean foundMapBound = false; // in case the map coordinates are not given in the file
            for (String element: fileWorld) {
                String[] temp = element.split(" "); // split each string using space to differentiate them
                int mapDimensionElements = 2;
                if (temp.length == mapDimensionElements) { // map coordinates
                    foundMapBound = true;
                    // generate a map with given width and height and also declare a 2-dimensional ArrayList
                    // to save map data
                    myGameWorldMap = new Map(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
                } else if (temp.length == 1) {
                    // terrain map data, save the data into an Entity type ArrayList
                    ArrayList<Entity> innerList= new ArrayList<>();
                    for (int i = 0; i < element.length(); i++) {
                        Entity mapItem = new Entity(element.substring(i, i + 1));
                        innerList.add(mapItem);
                    }
                    // initiate the 2-dim ArrayList in the current game world map
                    myGameWorldMap.setOriginalMapList(innerList);
                } else if (element.contains("player")) {
                    int xIndex = 1; int yIndex = 2;
                    player.setX(Integer.parseInt(temp[xIndex])); // possible number format exception
                    player.setY(Integer.parseInt(temp[yIndex]));
                } else if (element.contains("monster")) {
                    int xIndex = 1; int yIndex = 2; int nameIndex = 3; int healthIndex = 4; int damageIndex = 5;
                    int monsterX = Integer.parseInt(temp[xIndex]);
                    int monsterY = Integer.parseInt(temp[yIndex]);
                    String monsterName = temp[nameIndex];
                    int monsterHealth = Integer.parseInt(temp[healthIndex]);
                    int monsterDamage = Integer.parseInt(temp[damageIndex]);
                    Monster monster = new Monster(monsterName, monsterX, monsterY, monsterHealth, monsterDamage);
                    monstersList.add(monster); // set each monster into an temporary arraylist
                } else if (element.contains("item")){
                    int xIndex = 1; int yIndex = 2; int symbolIndex = 3;
                    int itemX = Integer.parseInt(temp[xIndex]);
                    int itemY = Integer.parseInt(temp[yIndex]);
                    String itemSymbol = temp[symbolIndex];
                    Item item = new Item(itemSymbol, itemX, itemY); // set each item into an temporary arraylist
                    itemsList.add(item);
                }
            }
            if (!foundMapBound) {
                System.out.println("Suitable map bounds not found， can't decide map-width and map-height");
            }
            // generate a new game world using information gained from the file
            myCurrentGameWorld = new World (myGameWorldMap, player, monstersList, itemsList);
        } catch (NumberFormatException e) {
            System.out.println("Some inputs which are supposed to be numbers in the file are not numbers");
        }
    }

    /**
     * To start a file-based game world by getting a deepcopy of original gameWorld map every time before
     * player or monster move and then set entities onto the copy map
     * @param scan
     */
    private void fileGameWorld (Scanner scan) {
        ArrayList<ArrayList<Entity>> copyMap = myCurrentGameWorld.insertEntities();
        printMap(copyMap);
        System.out.print("\n> ");
        String move = scan.nextLine().toLowerCase();

        while (move != null) {
            if (move.equalsIgnoreCase("home")) {
                player.setDamage(player.getDamage() + damageIncrement);
                displayReturnHome(scan);
                break;
            } else {
                myCurrentGameWorld.monsterMoveUpdateXy(); // update monsters coordinates
                myCurrentGameWorld.playerMoveUpdateXy(move); // updating player coordinates
                // check if any two entity overlaps and take accordingly actions
                if (checkOverlap(scan, copyMap)) {
                    move = null; // checkOverlap returns true only when player get the wrap stone or dead
                }
                else{
                    copyMap = myCurrentGameWorld.insertEntities();
                    printMap(copyMap);
                    System.out.print("\n> ");
                    move = scan.nextLine().toLowerCase();
                }
            }
        }
    }


    /**
     * To check whether two entities' coordinates overlap, if overlap take the according actions according to
     * specifications
     * @param scan
     * @param copyMap
     * @return true only when the game ends (i.e. player got the wrap stone or the player dead in the game)
     */
    public boolean checkOverlap(Scanner scan, ArrayList<ArrayList<Entity>> copyMap) {
        boolean end = false;
        int playerX = player.getX(); int playerY = player.getY();
        outLoop:
        for (Item eachItem: myCurrentGameWorld.getItemInfoInFile()) {
            int itemX = eachItem.getX(); int itemY = eachItem.getY();
            String itemIcon = eachItem.getIcon();
            if (playerX == itemX && playerY == itemY) { // check if player overlaps with any items
                switch (itemIcon) {
                    case "+":
                        System.out.println("Healed!");
                        player.setCurHealth(player.getMaxHealth());
                        myCurrentGameWorld.getItemInfoInFile().remove(eachItem); // remove the item from the list
                        break;
                    case "^":
                        System.out.println("Attack up!");
                        damageIncrement += 1;
                        myCurrentGameWorld.getItemInfoInFile().remove(eachItem);
                        break;
                    case "@": // the game ends as player got the wrap stone
                        System.out.println("World complete! (You leveled up!)");
                        player.setLevel(player.getLevel() + 1);
                        System.out.println();
                        System.out.println("(Press enter key to return to main menu)");
                        scan.nextLine();
                        end = true;
                        break outLoop; } } }
        for (Monster eachMonster: myCurrentGameWorld.getMonsterInfoInFile()) {
            // check if the player is overlap with any of the monsters, if overlapping ,the battle will start
            int monsterX = eachMonster.getX(); int monsterY = eachMonster.getY();
            if (player.getX() == monsterX && player.getY() == monsterY) {
                String game = "fileGame";
                battleStart(eachMonster, game, scan);
                if (eachMonster.getCurHealth() <= 0) {
                    myCurrentGameWorld.getMonsterInfoInFile().remove(eachMonster);
                    break;
                } else {
                    end = true; } } }
        for (Monster eachMonster: myCurrentGameWorld.getMonsterInfoInFile()) {
            // check if any item is overlap with any of the monsters, if overlapping, the monster's icon will be
            // displayed but nothing will be removed from the world
            for (Item eachItem: myCurrentGameWorld.getItemInfoInFile()) {
                int itemX = eachItem.getX(); int itemY = eachItem.getY();
                int monsterX = eachMonster.getX(); int monsterY = eachMonster.getY();
                if (itemX == monsterX && itemY == monsterY) {
                    copyMap.get(monsterY).set(monsterX, eachMonster);
                }
            }
        }
        return end;
    }


    /**
     * To print the map out with given preset map data in the form of an 2-dimensional arraylist
     * @param copyMap
     */
    private void printMap(ArrayList<ArrayList<Entity>> copyMap) {
        for (int row = 0; row < copyMap.size(); row++) {
            for (int col = 0; col < copyMap.get(row).size(); col++) {
                System.out.print(copyMap.get(row).get(col).getIcon());
            }
            System.out.println();
        }
    }

    /**
     * To display returning home message
     * @param scan
     */
    private void displayReturnHome(Scanner scan) {
        System.out.println("Returning home...\n");
        System.out.println("(Press enter key to return to main menu)");
        scan.nextLine();
    }

    /**
     * To heal the player and monster to the max health before next battle in the default game
     */
    private void healPlayerMonster() {
        // set the player back to its maxHealth of its current level
        player.setCurHealth(player.getMaxHealth());
        // set the monster back to its maxHealth
        monster.setCurHealth(monster.getMaxHealth());
    }

    /**
     * To display a battle start message to the user and start the battleLoop
     * @param currentMonster
     * @param game
     * @param scan
     */
    private void battleStart(Monster currentMonster, String game, Scanner scan) {
        System.out.println(player.getName() + " encountered a " +
                currentMonster.getName() + "!\n");
        while (player.getCurHealth() > 0
                && currentMonster.getCurHealth() > 0) {
            battleAttack(currentMonster, game, scan);
        }
    }

    /**
     * To timely update and display the status of both player and currentMonster during the battle
     * @param currentMonster
     * @param game
     * @param scan
     */
    private void battleAttack (Monster currentMonster, String game, Scanner scan) {
        displayBattleLoop(currentMonster);
        // check if both player and currentMonster's health are good before the player makes the next attack
        if (checkHealth(currentMonster, currentMonster.getName(), player.getName(), game, scan)) {
            System.out.println(player.getName() + " attacks " +
                    currentMonster.getName() + " for " + player.getDamage() + " damage.");
            // update the currentMonster's current health and check if both sides are good before the currentMonster
            // makes the next attack
            currentMonster.setCurHealth(currentMonster.getCurHealth() - player.getDamage());
            if (checkHealth(currentMonster, currentMonster.getName(), player.getName(), game, scan)) {
                System.out.println(currentMonster.getName() + " attacks " +
                        player.getName() + " for " + currentMonster.getDamage() + " damage.");
                // update the player's current health
                player.setCurHealth(player.getCurHealth() - currentMonster.getDamage());
                // check if both sides good to start another round of battle, print a line
                if (checkHealth(currentMonster, currentMonster.getName(), player.getName(), game, scan)) {
                    System.out.println(); }
            }
        }
    }

    /**
     * To display the status of both sides before each round of battle
     */
    private void displayBattleLoop (Monster currentMonster) {
        String playerInfo = player.getCurHealth() + "/"
                + player.getMaxHealth();
        String monsterInfo = currentMonster.getCurHealth() + "/" + currentMonster.getMaxHealth();
        System.out.println(player.getName() + " " + playerInfo + " | " +
                currentMonster.getName() + " " + monsterInfo);
    }

    /**
     * To check the current health level of both player and monster, return true if
     * both player and monster's health is greater than 0 i.e battle can continue
     * if any side current health is less than 0, returns false to stop the battle
     * and the winning message will be printed
     * @param monsterName
     * @param playerName
     * @return
     */
    private boolean checkHealth(Monster currentMonster, String monsterName, String playerName, String game, Scanner scan) {
        boolean healthOk = true;
        if (player.getCurHealth() <= 0) { // monster wins, return back to main menu
            System.out.println(monsterName + " wins!\n");
            System.out.println("(Press enter key to return to main menu)");
            if (game.equals("file")) {
                scan.nextLine();
            }
            healthOk = false;
        } else if (currentMonster.getCurHealth() <= 0) { // player wins, return back to the game world
            System.out.println(playerName + " wins!\n");
            if (game.equals("default")) {
                System.out.println("(Press enter key to return to main menu)");
            } else if (game.equals("file")) {
                fileGameWorld(scan);
            }
            healthOk = false;
        }
        return healthOk;
    }

}
