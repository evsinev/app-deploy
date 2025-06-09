package com.acme.appdeploy.servlet;

import com.acme.appdeploy.servlet.messages.ErrorResponse;
import com.payneteasy.apiservlet.IExceptionContext;
import com.payneteasy.apiservlet.IExceptionHandler;

import java.util.UUID;

import static com.acme.appdeploy.servlet.ErrorResponses.writeErrorResponse;

public class ApiExceptionJsonHandler implements IExceptionHandler {

    @Override
    public void handleException(Exception e, IExceptionContext aContext) {

//        IError error;
//        if (e instanceof ApiErrorException) {
//            ApiErrorException errorException = (ApiErrorException) e;
//            error = errorException.getError();
//
//        } else if (e instanceof JsonSyntaxException) {
//            error = BadRequestError.builder()
//                    .errorMessage("Bad incoming json: " + e.getMessage())
//                    .errorCorrelationId(UUID.randomUUID().toString())
//                    .build();
//        } else {
//            error = InternalSystemError.builder()
//                    .errorCode(-3)
//                    .errorMessage("Unknown error")
//                    .errorCorrelationId(UUID.randomUUID().toString())
//                    .build();
//        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId    ( UUID.randomUUID().toString() )
                .type       ( "ApiError." + e.getClass().getSimpleName() )
                .title      ( e.getMessage()               )
                .status     ( 500                          )
                .build();

        writeErrorResponse(e, errorResponse, aContext.getHttpResponse());
    }
}
