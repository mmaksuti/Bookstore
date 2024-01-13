package src.exceptions;
public class LastAdministratorException extends Exception {
    public LastAdministratorException() {
        super("Cannot delete the last administrator");
    }
}
