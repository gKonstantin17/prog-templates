package micros.plannerusers.keycloak;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;

import micros.plannerusers.dto.UserKcDTO;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class KeycloakUtils {
    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientID;
    @Value("${keycloak.credentials.secret}")
    private String clientSercret;

    private static Keycloak keycloak;// ссылка на единственный экземпляр кс
    private static RealmResource realmResource;
    private static UsersResource usersResource;

    @PostConstruct // выполнить метод после инициализации бина
    public Keycloak initKeycloak() {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .realm(realm)
                    .serverUrl(serverUrl)
                    .clientId(clientID)
                    .clientSecret(clientSercret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();

        }
        // Доступ к API
        realmResource = keycloak.realm(realm);
        usersResource = realmResource.users();
        return keycloak;
    }

    public Response createKeycloakUser (UserKcDTO user) {


        // данные пароля
        CredentialRepresentation credentialRepresentation = createPasswordCredetials(user.getUserpassword());

        // данные пользователя
        UserRepresentation kcUser = new UserRepresentation(); // контейнер для юзера
        kcUser.setUsername(user.getUsername());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);

        Response response = usersResource.create(kcUser);
        return response;
    }

    private CredentialRepresentation createPasswordCredetials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false); // не нужно менять пароль после первого входа
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
    public void addRoles(String userId, List<String> roles) {
        // читаем роли
        List<RoleRepresentation> kcRoles = new ArrayList<>();
        for (String role : roles) {
            // соотносим полученную роль с существующей в реалме
            RoleRepresentation roleRep = realmResource.roles().get(role).toRepresentation();
            kcRoles.add(roleRep);
        }
        // получаем пользователя - не UsersResource
        UserResource uniqueUserResource = usersResource.get(userId);
        // добавляем ему роли на уровне realm
        uniqueUserResource.roles().realmLevel().add(kcRoles);
    }


    public void deleteKeycloakUser(String userId) {
        UserResource uniqUserResource = usersResource.get(userId);
        uniqUserResource.remove();
    }

    public void updateKeycloakUser(UserKcDTO dto) {
        CredentialRepresentation credentialRepresentation = createPasswordCredetials(dto.getUserpassword());

        // обновлнение полей
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(dto.getUsername()); // не изменяемый
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(dto.getEmail());

        // получаем и обновляем пользователя
        UserResource userResource = usersResource.get(dto.getId());
        userResource.update(kcUser);

    }

    public UserRepresentation findUserById(String userId) {
        return usersResource.get(userId).toRepresentation();
    }

    public List<UserRepresentation> searchKeycloakUsers(String text) {
        return usersResource.searchByAttributes(text);
    }
}
