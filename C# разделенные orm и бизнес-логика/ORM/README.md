# ORM на EFcore
Перед созданием проекта:
**обновить Visual Studio и .net (sdk)**

**При копировании файлов с кодом, копировать только классы**

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


### 3. Контроллер
в [Route("api/clients")] указывается путь, куда будут идти запросы 

Здесь реализованы запросы:

**Get** - получить все записи из таблицы

**Get {id}** - получить конкретную запись по /id

**Post** - создать запись в таблице

**Put** - редактировать запись в таблице

**Delete** - удалить запись


## Миграция 
Открыть консоль (Ctrl + ~)

Прописать команды 

`cd [Название проекта]`

`dotnet ef migrations add InitialCreate`

`dotnet ef database update`