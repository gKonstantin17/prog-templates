Напрямую отправлять запросы между сервисами можно через
### RestTemplate
Внедряется из Utils

Вызов
```
if (userRest.userExist(category.getUserId()))  
		return ResponseEntity.ok(service.add(category));  
```

### WebClient
Внедряется из Utils

Вызов
```
if (userWeb.userExist(category.getUserId()))  
	return ResponseEntity.ok(service.add(category));  
```
### Feign
Внедряется как зависимость
`implementation 'org.springframework.cloud:spring-cloud-starter-feign:+'`

перед мейн классом добавить
`@EnableFeignClients`

внутри мс создаем пакет feign, создаем репозиторий и метод который вызываться
```
// если не модуль не видит, то добавить зависимость и ComponentScan  
@FeignClient(name = "planner-users")  
public interface UserFeignClient {  
	@PostMapping("/user/id") // слеш в начале нужен  
	ResponseEntity<User> findUserById(@RequestBody Long id);  
}
```
для обработки исключений используется `FeignExceptionHandler` из пакета `feign`


Вызов
```
if (userFeignClient.findUserById(category.getUserId()) != null)  
	return ResponseEntity.ok(service.add(category));  
```



[Назад](../README.md)