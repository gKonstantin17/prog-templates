package micros.plannertodo.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

@Component
public class FeignExceptionHandler implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 406: {
                return new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, readMessage(response));
            }
        }
        return null;
    }

    private String readMessage(Response response) {
        String message = null;
        Reader reader = null;

        try {
            reader = response.body().asReader(Charset.defaultCharset());
            message = String.valueOf(CharStreams.fromReader(reader));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return message;
    }
}
