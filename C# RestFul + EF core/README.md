# RestFul + EFcore
Перед созданием проекта:
**обновить Visual Studio и .net (sdk)**

## Настройка проекта:
### 1. Создать проект web-api (ASP.NET core). Если его нет, вписать в поиск
### 2. Установить пакеты NuGet:
- Microsoft.EntityFrameworkCore
- Microsoft.EntityFrameworkCore.Tools
- Microsoft.EntityFrameworkCore.Design
- В сочетании с postgres: Npgsql.EntityFrameworkCore.PostgreSQL

### 3. Создать Startup, ApplicationContext, Program
И скопировать содержимое
В Startup подкорректировать connectionString

**PostgreSQL**

`var connectionString = "Server=localhost;Port=5432;Database=KIS;User Id=postgres;Password=password;";`

### 4. Создать настроить видимость классов
ASP.NET автоматически внедряет зависимости для Program, Startup, ApplicationContext, Entity-классов. Остальные нужно внедрять в приложение самостоятельно:

в Startup `services.AddControllers();` - видеть все контроллеры

`services.AddScoped<SpecRepository>();` - видеть данный класс репозитория

`services.AddScoped<SpecService>();` - видеть данный класс репозитория

## Пример создания сущности (по таблице в бд):
### 1. Создаем папку с сущностью Entity 
- PK отмечаем аннотацией [Key]
- Если поле может содержать NULL, то после типа данных ставим "?"
- Если NOT NULL, то явно указываем **required** перед типом данных

Отношения между таблицами:

**1:1**

В качестве FK используется

поле, у которого тип данных - другая сущность 

`public Employee employee { get; set; }`

**1:N**

у таблицы с 1:

поле, у которого тип данных - List<другая сущность>

`public List<Order>? orders { get; set; }`

у таблицы с N:

`public Client client { get; set; }`

### 2. DTO - содержит только часть полей из entity-класса
используется для удобного отображения (без лишней информации)

Создаем папку DTO и подпапку с DTO для каждой сущности

Здесь например создана дтошка без внешнего ключа,чтобы избежать ошибки 500 при тестировании запросов


## Миграция 
Открыть консоль (Ctrl + ~)

Прописать команды 

`cd [Название проекта]`

`dotnet ef migrations add InitialCreate`

`dotnet ef database update`

Скачать весь проект:
[v1.0.0](https://github.com/gKonstantin17/prog-templates/releases/tag/v1.0.0)

