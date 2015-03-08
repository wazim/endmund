package net.wazim.endmund.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

public class EndmundResponseHandler extends DefaultResponseErrorHandler {
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        //do nothing
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        //do nothing
        return false;
    }

    @Override
    protected boolean hasError(HttpStatus statusCode) {
        //do nothing
        return false;
    }
}
