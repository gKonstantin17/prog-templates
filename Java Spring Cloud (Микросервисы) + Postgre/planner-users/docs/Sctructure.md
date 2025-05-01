репозиторий -> сервис -> контроллер - аналогично шаблону Spring Restful

Сущности описаны в модуле [Entity](../../planner-entity/README.md)
и подключены вместе с Utils как зависимости в build.gradle


Из модуля [Utils](../../planner-utils/README.md) используется Конвертер ролей Keycloak авторизации,
RestTemplate и WebClient для вызова Users


findByParam у Task возвращает страничный (Pageable) результат

принимает запросы от сервиса Todo



[Назад](../README.md)