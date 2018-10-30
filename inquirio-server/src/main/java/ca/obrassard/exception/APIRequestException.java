package ca.obrassard.exception;

public class APIRequestException extends Exception {
    public APIErrorCodes error;
    public String targetAttribute;
    public APIRequestException(APIErrorCodes error, String attribute) {
        super((error == APIErrorCodes.Forbidden || error == APIErrorCodes.AccesDenied) ?
                "Vous n'êtes pas autorisé à effectuer cette action ou vous n'êtes pas authentifié" :
                "Un ou plusieurs arguments de la requête sont invalides");
        this.error = error;
        this.targetAttribute = attribute;
    }
    public APIRequestException(APIErrorCodes error) {
        super((error == APIErrorCodes.Forbidden || error == APIErrorCodes.AccesDenied) ?
                "Vous n'êtes pas autorisé à effectuer cette action ou vous n'êtes pas authentifié" :
                "Un ou plusieurs arguments de la requête sont invalides");
        this.error = error;
        this.targetAttribute = null;
    }


    public ApiRequestExceptionResponse toResponse(){
        return new ApiRequestExceptionResponse(this);
    }

    static class ApiRequestExceptionResponse{
        public APIErrorCodes error;
        public String message;
        public String targetAttribute;
        public ApiRequestExceptionResponse(APIRequestException ex) {
            error = ex.error;
            message = ex.getMessage();
            targetAttribute = ex.targetAttribute;
        }
    }
}
