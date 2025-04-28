# JavaSE приложение с ORM Hibernate для postgres

Пример использования основных возможностей данной ORM.
также использовались Lombok,log4j2 и encache.

**Настройки:**

JDK 17

Библиотеки подключены через gradle (см файл build.gradle)

в src\main\resources\hibernate.cfg.xml

находится конфиг для Hibernate

`<property name="connection.*>` - настройки подключения к бд

`+` hibernate.default_schema и hibernate.default_catalog

сущности подключаются через <mapping class=""/>

настройки логирования
- `<property name="hibernate.show_sql">true</property>`
- `<property name="hibernate.format_sql">true</property>`
- `<property name="hibernate.highlight_sql">true</property>`
- `<property name="hibernate.use_sql_comments">true</property>`
- `<property name="hibernate.hibernate.session.events.log">true</property>`

для логирования можно использовать библиотеку log4j2

настройки которой задаются в

src\main\resources\log4j2.properties


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

### Подключение к бд
класс `HibernateUtil` является фабрикой для созданий сессий

в нем указывается путь к конфигурации hibernate.cfg.xml

далее в `Main` показан пример работы с сессией

### Выполнение запросов
- класс `Main`: транзакция c помощью JPA
- класс `HQL_API`: c помощью HQL_API
- класс `NativeSQL`: с помощью SQL запросов, заключенных в строковую переменную
- класс `CriteriaAPIi`: с помощью Criteria API
- пакет `dao`: создание Data Access Object для каждой сущности, в которых методы с запросами
(используется в `Scenario`)
