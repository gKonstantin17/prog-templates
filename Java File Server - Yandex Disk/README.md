# Документация API File Server - Yandex Disk Integration

## 📋 Содержание
1. [Общее описание](#общее-описание)
2. [Подключение к Яндекс.Диску](#подключение-к-яндекс-диску)
2. [Модель данных](#модель-данных)
3. [Эндпоинты API](#эндпоинты-api)

---

## Общее описание

Сервис предоставляет REST API для управления файлами на Яндекс.Диске с локальным кэшированием метаданных в базе данных PostgreSQL. Основные возможности:

- **Загрузка/скачивание файлов** с Яндекс.Диска
- **Управление папками** (создание, перемещение, удаление)
- **Древовидное представление** файловой структуры
- **Мягкое и жесткое удаление** с возможностью восстановления
- **Синхронизация** между Яндекс.Диском и локальной БД

---

## Подключение к Яндекс.Диску
- Переходим на https://oauth.yandex.ru/
- Создаем приложение
- Запрашиваемые права: всё, что связано с disk
- Сохраняем token ClientID
- Переходим на `https://oauth.yandex.ru/authorize?response_type=code&client_id=<ClientID>`, вставив свой ClientID
- Если не работает, уточнить по `https://yandex.ru/dev/id/doc/ru/codes/code-url#code-request`
- Получаем: OAuthToken `y0__xD...`
- OAuthToken вставляем в `application.properties` в поле `yandex.token=`
## Модель данных

### Сущность FileData

```
-- Table: public.filedata

-- DROP TABLE IF EXISTS public.filedata;

CREATE TABLE IF NOT EXISTS public.filedata
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    name text COLLATE pg_catalog."default",
    path text COLLATE pg_catalog."default",
    upload_date timestamp without time zone,
    type text COLLATE pg_catalog."default",
    size integer,
    service_name text COLLATE pg_catalog."default",
    parent_path text COLLATE pg_catalog."default",
    is_deleted boolean,
    CONSTRAINT filedata_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.filedata
    OWNER to postgres;
```

Таблица `filedata` в PostgreSQL хранит метаданные файлов и папок:

| Поле | Тип | Описание | Назначение |
|------|-----|----------|------------|
| `id` | Long | Уникальный идентификатор | Первичный ключ, используется для операций с конкретным файлом |
| `name` | String | Имя файла/папки | Отображается в интерфейсе, используется при скачивании |
| `path` | String | Полный путь на Яндекс.Диске | Уникальный идентификатор в Яндекс.Диске, начинается с `/` |
| `upload_date` | Timestamp | Дата загрузки/создания | Для сортировки и отслеживания возраста файлов |
| `type` | String | Тип ресурса | `FOLDER` для папок, расширение файла (txt, jpg) для файлов |
| `size` | Long | Размер в байтах | 0 для папок, для файлов - актуальный размер |
| `service_name` | String | Имя сервиса хранения | "YANDEX_DISK" - для расширяемости (можно добавить другие облака) |
| `parent_path` | String | Путь родительской папки | Для построения дерева без рекурсивных запросов |
| `is_deleted` | Boolean | Флаг мягкого удаления | false - активный, true - помечен на удаление |

### Почему такие поля?

- **`path` и `parent_path`**: Позволяют эффективно строить деревья и искать файлы по пути без множественных join'ов
- **`is_deleted`**: Реализует корзину - файлы не удаляются физически сразу, их можно восстановить
- **`service_name`**: Архитектурный задел для поддержки разных облачных хранилищ

---

## Эндпоинты API

Базовый URL: `/api/file`

### 1. 📤 Загрузка файла

**`POST /upload`**

Загружает файл на Яндекс.Диск и сохраняет метаданные в БД.

**Параметры:**
- `file` (multipart/form-data) - загружаемый файл
- `path` (query param, default="/") - путь для загрузки

**Пример запроса:**
```http
POST /api/file/upload?path=/documents
Content-Type: multipart/form-data

file: report.pdf
```

Ответ: 200 OK


```
{
  "message": "File uploaded successfully",
  "id": 123,
  "path": "/documents/report.pdf",
  "name": "report.pdf",
  "size": 1048576,
  "type": "pdf"
}
```

### 2. 📁 Создание папки
**`POST /folder`**

Создает новую папку на Яндекс.Диске и в БД.

Тело запроса:
```
json
{
  "path": "/documents"
}
```
Ответ: 200 OK

```
{
  {
  "message": "Folder created successfully",
  "id": 456,
  "path": "/documents/2024",
  "type": "FOLDER"
}
}
```

### 3. 📥 Скачивание файла
По ID (GET)
**`GET /download/{id}`**

Скачивает файл по его ID в базе данных.

Пример запроса:

```
http
GET /api/file/download/123
```

Ответ: файл в формате application/octet-stream

**По пути (POST)**
**`POST /download`**

Скачивает файл по полному пути.

Тело запроса:

```
json
{
  "path": "/documents/report.pdf"
}
```
### 4. 🔄 Перемещение файла
**`POST /move`**

Перемещает файл или папку.

Тело запроса:

```
json
{
  "from": "/documents/report.pdf",
  "path": "/archive/report.pdf",
  "overwrite": true,
  "forceAsync": false
}
```
Поля запроса:

- `from` - исходный путь

- `path` - путь назначения

- `overwrite` - перезаписывать если существует (default: false)

- `forceAsync` - выполнять асинхронно (default: false)

### 5. 🔄 Синхронизация

**`POST /sync`**

Синхронизирует метаданные файла с Яндекс.Диском - добавляет информацию о нем в бд, если ещё нет записи

Тело запроса:

```
json
{
  "path": "/documents/report.pdf"
}
```

Ответ:

```
json
{
  "message": "Resource ADDED to database",
  "data": {
    "name": "report.pdf",
    "path": "/documents/report.pdf",
    "type": "file",
    "size": 1048576
  }
}
```

### 6. 🗑️ Удаление файлов
Удаление по ID

**`DELETE /delete/{id}`**

Удаляет файл с Яндекс.Диска и из БД.

Пример запроса:

```
http
DELETE /api/file/delete/123
```

Удаление по пути

**`DELETE /delete`**


Тело запроса:

```
json
{
  "path": "/documents/report.pdf",
  "permanently": true,
  "forceAsync": false
}
```

Мягкое удаление (пометить на удаление)

**`DELETE /soft-delete/{id}`** и **`DELETE /soft-delete`**

Помечает файл как удаленный в БД (is_deleted = true), на Яндекс.Диске файл остается.

### 7. ♻️ Восстановление
**`POST /restore`**`

Восстанавливает файлы, помеченные на удаление.

Тело запроса:

список path:

```
json
["/documents/report.pdf", "/documents/archive.tar.gz"]
```
### 8. 🧹 Очистка корзины
**`DELETE /clean-soft-delete`**

Удаляет с Яндекс.Диска все файлы, помеченные на удаление в БД. 

Без параметров

### 9. 🌳 Получение структуры файлов
Полное дерево
**`GET /get-full`**`

Возвращает все файлы и папки в виде дерева.

Без параметров

Дерево от пути
**`GET /get-tree`**`

Тело запроса:

```
json
{
  "path": "/documents/report.pdf"
}
```

Возвращает содержимое указанной папки.

Ответ:
```
json
{
  "roots": [
    {
      "id": 456,
      "name": "2024",
      "path": "/documents/2024",
      "type": "FOLDER",
      "size": 0,
      "children": [
        {
          "id": 789,
          "name": "january",
          "path": "/documents/2024/january",
          "type": "FOLDER",
          "size": 0,
          "children": []
        }
      ]
    }
  ]
}
```

### 10. 🔍 Информация о ресурсе
**`GET /find-in-service`**

Получает актуальную информацию о ресурсе с Яндекс.Диска.

Тело запроса:

```
json
{
  "path": "/documents/report.pdf"
}
```
