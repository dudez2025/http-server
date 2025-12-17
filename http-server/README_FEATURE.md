# Добавление поддержки Query параметров в HTTP-сервер

## Изменения

### 1. Новая функциональность
- Добавлена поддержка парсинга query параметров из URL
- Реализованы методы в классе `Request`:
  - `getQueryParam(String name)` - получение первого значения параметра
  - `getQueryParams()` - получение всех параметров в виде Map
  - `getQueryParamMultiple(String name)` - получение всех значений параметра
- Добавлена обработка endpoint `/messages` с поддержкой query параметров

### 2. Технические изменения
- Добавлена зависимость Apache HttpClient для использования `URLEncodedUtils`
- Обновлен класс `Request` для разделения пути и query параметров
- Обновлен класс `Server` для проверки путей без query параметров
- Добавлен новый endpoint `/messages` для демонстрации работы

### 3. Тестирование
- Добавлены unit-тесты для проверки парсинга query параметров
- Создана демонстрационная страница `messages.html`
- Обновлен `index.html` с примерами ссылок с параметрами

## Примеры использования

```java
// Создание запроса с query параметрами
Request request = new Request("GET", "/messages?last=10&sort=asc", "HTTP/1.1");

// Получение параметров
String last = request.getQueryParam("last"); // "10"
String sort = request.getQueryParam("sort"); // "asc"
Map<String, List<String>> allParams = request.getQueryParams();