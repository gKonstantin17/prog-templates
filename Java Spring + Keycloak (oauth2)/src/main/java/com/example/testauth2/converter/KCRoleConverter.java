package com.example.testauth2.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KCRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    // принимать данные и конвертировать в список ролей GrantedAuthority
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
// доступ к
//        "resource_access": {
//            "realm-management": {
//                "roles": [
//                "view-realm",
//                "view-identity-providers",
        Map<String,Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");

        if (realmAccess == null || realmAccess.isEmpty())
            return new ArrayList<>(); // нет ролей, если раздел json не найден

        Collection<GrantedAuthority> returnValue = ((List<String>) realmAccess.get("roles"))
                .stream().map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return returnValue;
    }
}
