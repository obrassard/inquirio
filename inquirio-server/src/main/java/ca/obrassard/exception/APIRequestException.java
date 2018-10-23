package ca.obrassard.exception;

public class APIRequestException extends Exception {
    public APIErrorCodes[] errors;
    public APIRequestException(APIErrorCodes... args) {
        super("Un ou plusieurs arguments de la requête sont invalides");
        this.errors = args;
    }

    public ApiRequestExceptionResponse toResponse(){
        return new ApiRequestExceptionResponse(this);
    }

    static class ApiRequestExceptionResponse{
        public APIErrorCodes[] errors;
        public String message;

        public ApiRequestExceptionResponse(APIRequestException ex) {
            errors = ex.errors;
            message = ex.getMessage();
        }
    }
}
