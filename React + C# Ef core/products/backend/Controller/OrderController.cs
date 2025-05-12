using kis.DTO.Order;
using kis.Service;
using Microsoft.AspNetCore.Mvc;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace kis.Controller
{
    [Route("api/order")]
    [ApiController]
    public class OrderController : ControllerBase
    {
        private OrderService service;
        public OrderController(OrderService service)
        {
            this.service = service;
        }

        // GET: api/order/get
        [HttpGet("get")]
        public async Task<IActionResult> get()
        {
            var result = await service.get();
            return Ok(result);
        }

        // POST api/order/create
        [HttpPost("create")]
        public async Task<IActionResult> create([FromBody] OrderCreateDto dto)
        {
            if (dto.Count <= 0)
                return StatusCode(403, "Количество должно быть > 0");
            if (dto.Price < 0)
                return StatusCode(403, "Цена должна быть >= 0");
            if (dto.Product_id == null)
                return StatusCode(403, "Товар должен быть заказан");

            var (result,message) = await service.create(dto);
            if (message != "Ok")
                return BadRequest(message);

            return Ok(result);
        }

        // PUT api/order/update/5
        [HttpPut("update/{id}")]
        public async Task<IActionResult?> update(long id, [FromBody] OrderUpdateDto order)
        {
            if (id <= 0)
                return NotFound("id должен >= 0");
            if (order.Count <= 0)
                return StatusCode(403, "Количество должно быть > 0");
            if (order.Price < 0)
                return StatusCode(403, "Цена должна быть >= 0");
            // можно изменять любое поле
            var result = await service.update(id, order);
            if (result.Error != null)
                return StatusCode(403, result.Error);

            return Ok(result.Order);
        }

        // DELETE api/order/delete/5
        [HttpDelete("delete/{id}")]
        public async Task<IActionResult?> delete(int id)
        {
            if (id <= 0)
                return NotFound("id должен >= 0");

            var result = await service.delete(id);
            if (result == null)
                return NotFound("Записи не существует");
            return Ok(result);
        }
    }
}
