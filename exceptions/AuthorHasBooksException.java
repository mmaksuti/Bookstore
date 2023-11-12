package exceptions;

public class AuthorHasBooksException extends Exception {
    public AuthorHasBooksException() {
        super("Cannot delete an author with books");
    }
}
