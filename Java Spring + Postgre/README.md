# Java Spring приложение с ORM Hibernate для postgres

CRUD операции в бд с помощью http запросов

**Настройки:**

JDK 17

Зависимости Spring:

Developer Tools:
- Spring Boot DevTools
- Spring Configuration Processor
- Lombok 
Web: 
- Spring Web
Security:
- Sprint Security
SQL:
- Spring Data JPA 
- PostgreSQL Driver - или для любой подходящей бд

Библиотеки подключены через gradle (см файл build.gradle)

**application.properties**

указывает порт и подключает профиль local, в котором:

`spring.datasource.*` - настройки подключения к бд

`server.ssl.*` - настройки подключения ssl

логи запрос через

`spring.jpa.*` - через jpa

или

`logging.level.*` - через логирование встроеное в Spring библиотеки

чтобы записи указывались в нужном **часовом поясе**:

`spring.jackson.time-zone=Europe/Moscow`


## Сущности
в каталоге entity созданные классы - сущности таблиц из бд

можно сгенерировать с помощью ide по готовым таблицам из бд

используются следующие **аннотации**:

### Табличные:
- `@Entity` для обозначения что это сущность
- `@Table` -  привязывает класс и таблицу, что позволяет переименовать класс в параметрах указываем к какой таблице относится (название таблицы, схема, бд)
- `@Basic` – дополнительные параметры (ленивая загрузка, разрешение на NULL)


#### Колонки:
- `@Column` - уточняет название колонки, можно задать ограничение null и по коль-ву символов
- `@Id` и @GeneratedValue - для обозначения поля с id и как его заполнять
- `@Convert` - Конвертация типов

Например: из бд 0/1 преобразуется в false/true
- `@Convert(converter = org.hibernate.type.NumericBooleanConverter.class)`

`private boolean activated;`

#### FK
указывается тип связи и по каким столбцам связываеются сущности

**1:N**

`@ManyToOne`  

`@JoinColumn(name = "user_id", referencedColumnName = "id")`  

`private User user;`

где User, другой `@Entity`

у другой сущности

`@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)`  

`private List<Category> categories;`

**1:1**

`@OneToOne(fetch = FetchType.LAZY)`

`@MapsId`

`@JoinColumn(name = "user_id", referencedColumnName = "id")`

`private User user;`

у другой сущности(не обязательно, даже не желательно 

т.к. при доставании сущности за ней может потянуться другая сущность, что приведет к переполнению стека)

`@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = false,`
`          cascade = CascadeType.ALL)`

`private Activity activity;`


**N:N**

связывание происхоидит через промежуточную таблицу

`@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)`

`@JoinTable(name = "user_role",`

`           joinColumns = @JoinColumn(name = "user_id"),`

`           inverseJoinColumns = @JoinColumn(name = "role_id"))`

у другой сущности(также не обязательно)

`@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)`

`private Set<User> users;`


Также чтобы избежать зацикливание при доставании записей
переопределены методы equals и hashCode


### Кеширование
включается при добавление сущностям

`@Cacheable`
`@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)`


### Выполнение запросов
пакет `repository` - интерфейсы, с использованием JPQL, вызываются из `service`

пакет `service` - для обработки данных, вызываются из `controller`

пакет `controller` - получают запросы и отправляют ответы, используя выше перечисленную структуру

класс `aop/LoggingAspect` - позволяет логгировать вызовы из контроллеров (рефлексия)





