# Сервис оплаты Yokassa

Выполнение оплаты через Юкасса. Создание ссылки на страницу оплаты, получение результата оплаты, проверка платежа.

- Java 17
- Spring
- React
- Используется SDK [от dynamake](https://github.com/dynomake/yookassa-java-sdk)
- cloudpub (аналог ngrok)

### Настройка проекта
#### Создание тестового магазина Юкасса:

нужно зарегистрироваться по специальной ссылке, которую можно найти [здесь](https://yookassa.ru/docs/support/merchant/payments/implement/test-store)

сохранить **ShopId** и **секретный ключ API**

используя cloudpub, публикуем проект backend (порт 8081) и frontend (порт 3000)

на странице тестового магазина подключить HTTP-уведомления, добавив к URL ,бекенда `https://YOUR_DOMAIN.cloudpub.ru/api/yokassa/notification`

#### Backend
в `backend\src\main\resources\application.properties`
находятся поля:
```
YOU_SHOP_IDENTIFIER=13101572
YOU_SHOP_TOKEN=test_LL8du42mCBdnFfKBOk01kZsPeZrS9JFnibUbAYhLUyM3
```
они соотвествуют ShopId и секретный ключ API

#### Frontend
в `frontend\src\app\App.js` указаны URL для запросов 
```
fetch('https://videos.cloudpub.ru/api/payment/buy',...)
```
здесь также нужно сменить URL бекенда, полученного в cloudpub

### Запуск Backend
```
gradlew build
gradlew bootRun
```
### Запуск Frontend
```
npm install
npm start
```
### Полезные ссылки

- [документация Yokassa](https://yookassa.ru/developers/using-api/using-sdks)
- [api](https://yookassa.ru/my/gate/integration/api-keys)
- [создание тестового магазина](https://yookassa.ru/docs/support/merchant/payments/implement/test-store)
- [тестовыве карты оплаты](https://yookassa.ru/developers/payment-acceptance/testing-and-going-live/testing#test-bank-card-data)
- [тестовый магазин, просмотр логов](https://yookassa.ru/my/payments)
