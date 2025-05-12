using kis.DTO;
using kis.Service;
using Microsoft.AspNetCore.Mvc;

namespace kis.Controller
{
    [Route("api/order")]
    [ApiController]
    public class OrderController : ControllerBase
    {
        // контроллер для Заказов принимает запросы и отправляет ответы
        // данные для ответа формируются методами из сервиса
        private OrderService service;
        public OrderController(OrderService service)
        {
            this.service = service;
        }

        // GET: api/order/get
        [HttpGet("get")]
        public async Task<IActionResult> get()
        {
            // достать список заказов
            var result = await service.get();
            return Ok(result);
        }

        // POST api/order/create
        [HttpPost("create")]
        public async Task<IActionResult> create([FromBody] OrderCreateDto dto)
        {
            // создает заказ
            // из обязательных полей: Product_id
            // CreateDate по умолчанию - текущее время
            // Deadline по умолчанию - +неделя от CreateDate
            // Count по умолчанию - 1
            // Price по умолчанию - 100
            // FullPrice по умолчанию - автоматически вычисляется (Count * Price)

            // нельзя выставить Deadline раньше CreateDate
            var result = await service.create(dto);
            if (result == null)
                return BadRequest("Что-то пошло не так");

            return Ok(result);
        }

        // PUT api/order/update/5
        [HttpPut("update/{id}")]
        public async Task<IActionResult?> update(long id, [FromBody] OrderCreateDto order)
        {
            // изменить заказ
            // можно изменять любое поле
            // нельзя выставить Deadline раньше CreateDate
            var result = await service.update(id, order);
            if (result== null)
                return BadRequest("Что-то пошло не так");

            return Ok(result);
        }

        // DELETE api/order/delete/5
        [HttpDelete("delete/{id}")]
        public async Task<IActionResult?> delete(int id)
        {
            // удаление заказа
            var result = await service.delete(id);
            if (result == null)
                return NotFound("Записи не существует");
            return Ok(result); // если удален успешно, показать id
        }
    }
}
