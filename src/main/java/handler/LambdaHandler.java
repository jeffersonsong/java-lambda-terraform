package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author pramesh-bhattarai
 */
public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static String URL = "http://dummy.restapiexample.com/api/v1/employees";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent inputStream, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log(inputStream.toString());

        APIGatewayProxyResponseEvent response;
        try {
            String payload = fetchResponseFromUrl(URL, logger);
            response = createResponseObject(payload, 200);
        } catch (IOException e) {
            logger.log(e.getMessage());
            String payload = e.getMessage();
            response = createResponseObject(payload, 500);
        }
        logger.log("sending response");
        logger.log(response.toString());
        return response;
    }

    private String fetchResponseFromUrl(String url, LambdaLogger logger) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        logger.log("fetching response from :: " + url);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream())
            )) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            logger.log("response from :: " + url);
            logger.log(response.toString());

            return response.toString();
        } else {
            logger.log("unable to get response :: ");
            return con.getResponseMessage();
        }
    }

    private APIGatewayProxyResponseEvent createResponseObject(String payload, Integer statusCode) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setBody(payload);
        response.setStatusCode(statusCode);
        return response;
    }

}
