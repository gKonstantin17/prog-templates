using kis.DTO;
using kis.Service;
using Microsoft.AspNetCore.Mvc;

namespace kis.Controller
{
    [Route("api/warehouse")]
    [ApiController]
    public class WarehouseController : ControllerBase
    {
        // контроллер для Склада принимает запросы и отправляет ответы
        // данные для ответа формируются методами из сервиса
        private readonly WarehouseService service;
        public WarehouseController(WarehouseService service)
        {
            this.service = service;
        }

        // POST api/warehouse/get-by-date
        [HttpPost("get-by-date")]
        public async Task<IActionResult> findByDate([FromBody] DateTime date)
        {
            // показать что есть в складе на указанную дату
            // для этого перебираются все записи до указанной даты
            // и вычисляется итоговое количество товара на складе
            var result= await service.findByDate(date);
            if (result == null)
                return Ok("Что-то пошло не так");
            return Ok(result);
        }

        // GET api/warehouse/get-history/5                             
        [HttpGet("get-history/{id}")]
        public async Task<IActionResult> getHistoryByProductId(long id)
        {
            // история изменений товара на склада
            var result = await service.getHistoryByProductId(id);
            return Ok(result);
        }

        // GET api/warehouse/check-component
        [HttpGet("check-products")]
        public async Task<IActionResult> checkProducts()
        {
            // проверка хватает ли товара на складе, 
            // чтобы выполнить все заказы
            // выводит список id товаров с количеством, сколько не хватает
            var result = await service.checkProducts();
            if (result == null)
                return Ok("Что-то пошло не так");
            return Ok(result);
        }

        // POST api/warehouse/change
        [HttpPost("change")]
        public async Task<IActionResult> change([FromBody] WarehouseChangeDto dto)
        {
            // изменить количество товара на складе
            // для этого задается id товара
            // и ± количество 
            // если количество отрицательное, то проверка хватит ли товара
            // если товара ещё нет на складе, то создается
            var result = await service.change(dto);
            if (result == null)
                return Ok("Что-то пошло не так");
            return Ok(result);
        }


        // DELETE api/warehouse/delete/5
        [HttpDelete("delete/{id}")]
        public async Task<IActionResult?> delete(int id)
        {
            // удаление записи из истории изменений
            var result = await service.delete(id);
            if (result == null) return Ok("Не найдено");
            return Ok(result); // если удален успешно, показать id
        }
    }
}
