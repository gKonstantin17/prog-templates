using kis.DTO;
using kis.Service;
using Microsoft.AspNetCore.Mvc;

namespace kis.Controller
{
    [Route("api/product")]
    [ApiController]
    public class SpecController : ControllerBase
    {
        // контроллер для Товаров принимает запросы и отправляет ответы
        // данные для ответа формируются методами из сервиса
        private readonly SpecService service;
        public SpecController(SpecService service)
        {
            this.service = service;
        }

        // GET api/product/get-all-hierarchy
        [HttpGet("get")]
        public async Task<IActionResult> getAllHierarchy()
        {
            // получить все товары в виде иерархического дерева
            var result = await service.getAllHierarchy();
            return Ok(result);
        }

        [HttpPost("count-components")]
        public async Task<IActionResult> countComponents([FromBody] int id)
        {
            // посчитать коль-во подкомпонентов
            // выведет выбранный товар и список всех листьев с итоговым количеством
            var result = await service.countComponentsById(id);
            if (!result.Specifications.Any()) // проверка на наличие подкомпонентов
                return Ok("Нет подкомпонентов");
            return Ok(result);
        }

        // POST api/product/create
        [HttpPost("create")]
        public async Task<IActionResult> create([FromBody] SpecDto spec)
        {
            // добавить товар
            // проверка целостности данных: 
            // parent должен существовать в бд
            // level должен быть на 1 больше, чем у parent
            // самый нижний level - 0
            var result = await service.create(spec);
            if (result == null)
                return Ok("Возникла ошибка");
            return Ok(result);
        }


        // PUT api/product/update/5
        [HttpPut("update/{id}")]
        public async Task<IActionResult?> update(long id, [FromBody] SpecDto spec)
        {  // изменить любые поля товара
            var result = await service.update(id, spec);
            return Ok(result);
        }

        // DELETE api/product/delete/5
        [HttpDelete("delete/{id}")]
        public async Task<IActionResult?> delete(int id)
        {
            // каскадное удаление - удаление не только товара,
            // но и всех дочерних элементов
            var result = await service.delete(id);
            if (result == null)
                return Ok("Записи не существует");
            return Ok(result); // если удалены успешно, показать их id 
        }
    }
}
