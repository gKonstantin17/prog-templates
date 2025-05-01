# Utils

#### RestTemplate
используется как брокер сообщений между микросервисами
```
private static final String baseUrl = "http://localhost:8765/planner-users/user/";  
	public boolean userExist(Long userId) {  
		RestTemplate restTemplate = new RestTemplate();  
		HttpEntity<Long> request = new HttpEntity(userId);  
		  
		ResponseEntity<User> response = null;  
		  
		try {  
			response = restTemplate.exchange(baseUrl+"/id", HttpMethod.POST,request, User.class);  
			if (response.getStatusCode() == HttpStatus.OK)  
				return true;  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return false; // если статус != 200  
	}  
	
### Вызов 
if (userRest.userExist(category.getUserId()))  
    return ResponseEntity.ok(service.add(category));  
```
***
[RootProject](../README.md)