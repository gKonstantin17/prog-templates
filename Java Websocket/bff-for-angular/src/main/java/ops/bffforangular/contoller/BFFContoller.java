package ops.bffforangular.contoller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ops.bffforangular.dto.DataResult;
import ops.bffforangular.dto.Operation;
import ops.bffforangular.dto.SearchValue;
import ops.bffforangular.dto.UserProfile;
import ops.bffforangular.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bff")
public class BFFContoller {

    private static final RestTemplate restTemplate = new RestTemplate(); // для выполнения веб запросов на KeyCloak

    public static final String IDTOKEN_COOKIE_KEY = "IT";
    public static final String REFRESHTOKEN_COOKIE_KEY = "RT";
    public static final String ACCESSTOKEN_COOKIE_KEY = "AT";

    @Value("${resourceserver.url}")
    private String resourceServerURL;
    private String userId;

    @Value("${keycloak.secret}")
    private String clientSecret;

    @Value("${keycloak.url}")
    private String keyCloakURI;


    @Value("${client.url}")
    private String clientURL;

    @Value("${keycloak.clientid}")
    private String clientId;

    @Value("${keycloak.granttype.code}")
    private String grantTypeCode;

    @Value("${keycloak.granttype.refresh}")
    private String grantTypeRefresh;

    private final CookieUtils cookieUtils; // класс-утилита для работы с куками

    // срок годности куков
    private int accessTokenDuration;
    private int refreshTokenDuration;


    // временно хранят значения токенов
    private String accessToken;
    private String idToken;
    private String refreshToken;

    // используется, чтобы получать любые значения пользователя из JSON
    private JSONObject payload;


    @Autowired
    public BFFContoller(CookieUtils cookieUtils) {
        this.cookieUtils = cookieUtils;
    }

    @PostMapping("/token")
    public ResponseEntity<String> token(@RequestBody String code) {// получаем auth code, чтобы обменять его на токены
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // параметры запроса
        MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
        mapForm.add("grant_type", grantTypeCode);
        mapForm.add("client_id", clientId);
        mapForm.add("client_secret", clientSecret); // используем статичный секрет (можем его хранить безопасно), вместо code verifier из PKCE
        mapForm.add("code", code);

        // redirect - ТОЧНО ТАКОЙ ЖЕ URL, ИЗ КОТОРОГО ДЕЛАЕТСЯ ЗАПРОС СЮДА
        mapForm.add("redirect_uri", clientURL);
        //mapForm.add("redirect_uri", "http://localhost:4200/login");

        // добавляем в запрос заголовки и параметры
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapForm, headers);

        // выполняем запрос
        ResponseEntity<String> response = restTemplate.exchange(keyCloakURI + "/token", HttpMethod.POST, request, String.class);
        // мы получаем JSON в виде текста

        // сам response не нужно возвращать, нужно только оттуда получить токены
        parseResponse(response);

        // считать данные из JSON и записать в куки
        HttpHeaders responseHeaders = createCookies();

        // отправляем клиенту данные пользователя (и jwt-кук в заголовке Set-Cookie)
        return ResponseEntity.ok().headers(responseHeaders).build();
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue("IT") String idToken) {
        // Не работает с настройкой Consent Required в клиента KeyCloak
        // 1. закрыть сессии в KeyCloak для данного пользователя
        // 2. занулить куки в браузере

        // чтобы корректно выполнить GET запрос с параметрами - применяем класс UriComponentsBuilder
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(keyCloakURI + "/logout")
                .queryParam("post_logout_redirect_uri", "{post_logout_redirect_uri}")
                .queryParam("id_token_hint", "{id_token_hint}")
                .queryParam("client_id", "{client_id}")
                .encode()
                .toUriString();

        // конкретные значения, которые будут подставлены в параметры GET запроса
        Map<String, String> params = new HashMap<>();
        params.put("post_logout_redirect_uri", clientURL+"/login"); // может быть любым, т.к. frontend получает ответ от BFF, а не напрямую от Auth Server
        params.put("id_token_hint", idToken); // idToken указывает Auth Server, для кого мы хотим "выйти"
        params.put("client_id", clientId);

        // выполняем запрос (результат нам не нужен)
        try {
            restTemplate.getForEntity(
                    urlTemplate, // шаблон GET запроса - туда будут подставляться значения из params
                    String.class, // нам ничего не возвращается в ответе, только статус, поэтому можно указать String
                    params // какие значения будут подставлены в шаблон GET запроса
            );
        } catch (Exception e) {
            System.out.println("\n\nConnection refused: connect\n\n");
        }


        // занулить значения и сроки годности всех куков (тогда браузер их удалит автоматически)
        HttpHeaders responseHeaders = clearCookies();

        // отправляем браузеру ответ с пустыми куками для их удаления (зануления), т.к. пользователь вышел из системы
        return ResponseEntity.ok().headers(responseHeaders).build();
    }

    @GetMapping("/exchange")
    public ResponseEntity<String> exchangeRefreshToken(@CookieValue("RT") String oldRefreshToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // параметры запроса (в формате ключ-значение)
        MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
        mapForm.add("grant_type", grantTypeRefresh);
        mapForm.add("client_id", clientId);
        mapForm.add("client_secret", clientSecret);
        mapForm.add("refresh_token", oldRefreshToken);

        // собираем запрос для выполнения
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(keyCloakURI + "/token", HttpMethod.POST, request, String.class);

        // сам response не нужно возвращать, нужно только оттуда получить токены
        parseResponse(response);

        // создаем куки для их записи в браузер (frontend)
        HttpHeaders responseHeaders = createCookies();

        // отправляем клиенту ответ со всеми куками (которые запишутся в браузер автоматически)
        // значения куков с новыми токенами перезапишутся в браузер
        return ResponseEntity.ok().headers(responseHeaders).build();


    }
    @PostMapping("/data")
    public ResponseEntity<DataResult> data(@RequestBody SearchValue searchValue, @CookieValue("AT") String accessToken) {
        // обязательно нужно добавить заголовок авторизации с access token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // слово Bearer будет добавлено автоматически
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SearchValue> request = new HttpEntity<>(searchValue,headers);

        ResponseEntity<DataResult> response = restTemplate.postForEntity(resourceServerURL+ "/user/data", request, DataResult.class);

        return response;
    }

    @PostMapping("/operation")
    public ResponseEntity<Object> operation(@RequestBody String reqOperation, @CookieValue("AT") String accessToken) {
        ObjectMapper mapper = new ObjectMapper();
        Operation operation = new Operation();
        try {
            JsonNode root = mapper.readTree(reqOperation);
            HttpMethod httpMethod = HttpMethod.values()[root.get("httpMethod").asInt()];

            operation.setHttpMethod(httpMethod);
            operation.setUrl(root.get("url").asText());
            operation.setBody(root.get("body").asText());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // обязательно нужно добавить заголовок авторизации с access token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // слово Bearer будет добавлено автоматически
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request;

        if (operation.getBody() != null) {
            request = new HttpEntity<>(operation.getBody(),headers);
        } else {
            request = new HttpEntity<>(headers);
        }

        ResponseEntity<Object> response = restTemplate.exchange(operation.getUrl(), operation.getHttpMethod(),request, Object.class);
        return response;
    }

    @PostMapping("/stat")
    public ResponseEntity<Object> stat(@RequestBody Operation operation, @RequestBody String email, @CookieValue("AT") String accessToken) {

        // заголовок авторизации с access token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // слово Bearer будет добавлено автоматически

        // специальный контейнер для передачи объекта внутри запроса
        HttpEntity<Object> request = new HttpEntity<>(email, headers);

        // получение бизнес-данных пользователя (ответ обернется в DataResult)
        ResponseEntity<Object> response = restTemplate.postForEntity(operation.getUrl() + "/stat", request, Object.class);

        return response;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> profile() {
        if (idToken == null){
            return new ResponseEntity("access token not found", HttpStatus.NOT_ACCEPTABLE);
        }
        userId = getPayloadValue("sid"); // payload получили, когда работали с токенами
        UserProfile userProfile = new UserProfile(
                getPayloadValue("preferred_username"),
                getPayloadValue("email"),
                userId
        );
        return ResponseEntity.ok(userProfile);
    }

    // прочитать значения из токена
    private String getPayloadValue(String claim) {
        try {
            return payload.getString(claim);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // получение нужных полей из ответа KC
    private void parseResponse(ResponseEntity<String> response) {

        // парсер JSON
        ObjectMapper mapper = new ObjectMapper();

        // сначала нужно получить корневой элемент JSON
        try {
            JsonNode root = mapper.readTree(response.getBody());

            // получаем значения токенов из корневого элемента JSON в формате Base64
            accessToken = root.get("access_token").asText();
            idToken = root.get("id_token").asText();
            refreshToken = root.get("refresh_token").asText();

            // Сроки действия для токенов берем также из JSON
            // Куки станут неактивные в то же время, как выйдет срок действия токенов в KeyCloak
            accessTokenDuration = root.get("expires_in").asInt();
            refreshTokenDuration = root.get("refresh_expires_in").asInt();

            // все данные пользователя (профайл)
            String payloadPart = idToken.split("\\.")[1]; // берем значение раздела payload в формате Base64
            String payloadStr = new String(Base64.getUrlDecoder().decode(payloadPart)); // декодируем из Base64 в обычный текст JSON
            payload = new JSONObject(payloadStr); // формируем удобный формат JSON - из него теперь можно получать любе поля
        } catch (JsonProcessingException | JSONException e) {
            throw new RuntimeException(e);
        }

    }
    private HttpHeaders createCookies() {

        // создаем куки, которые браузер будет отправлять автоматически на BFF при каждом запросе
        HttpCookie accessTokenCookie = cookieUtils.createCookie(ACCESSTOKEN_COOKIE_KEY, accessToken, accessTokenDuration);
        HttpCookie refreshTokenCookie = cookieUtils.createCookie(REFRESHTOKEN_COOKIE_KEY, refreshToken, refreshTokenDuration);
        HttpCookie idTokenCookie = cookieUtils.createCookie(IDTOKEN_COOKIE_KEY, idToken, accessTokenDuration); // задаем такой же срок, что и AT

        // чтобы браузер применил куки к бразуеру - указываем их в заголовке Set-Cookie в response
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, idTokenCookie.toString());

        return responseHeaders;
    }


    // зануляет все куки, чтобы браузер их удалил у себя
    private HttpHeaders clearCookies() {
        // зануляем куки, которые отправляем обратно клиенту в response, тогда браузер автоматически удалит их
        HttpCookie accessTokenCookie = cookieUtils.deleteCookie(ACCESSTOKEN_COOKIE_KEY);
        HttpCookie refreshTokenCookie = cookieUtils.deleteCookie(REFRESHTOKEN_COOKIE_KEY);
        HttpCookie idTokenCookie = cookieUtils.deleteCookie(IDTOKEN_COOKIE_KEY);

        // чтобы браузер применил куки к бразуеру - указываем их в заголовке Set-Cookie в response
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, idTokenCookie.toString());
        return responseHeaders;
    }

}
