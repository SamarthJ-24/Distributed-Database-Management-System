package Registration;

class UserExistsException extends Exception {
    public UserExistsException(String errorMessage) {
        super(errorMessage);
    }
}
