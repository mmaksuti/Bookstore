package main;
public class UnauthenticatedException extends Exception {
    public UnauthenticatedException() {
        super("You are not authenticated.");
    }
}
