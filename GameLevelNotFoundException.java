package rogueProject;

/**
 * A self-defined Exception class for the exceptions throws in our program, in particular the FileNotFindException
 * @author: Yingxue Chen, yingxuec1@student.unimelb.edu.au, 1210613
 */
public class GameLevelNotFoundException extends Exception {

    /**
     * constructors
     */
    public GameLevelNotFoundException() {
        super();
    }

    public GameLevelNotFoundException(String message) {
        super(message);
    }
}
