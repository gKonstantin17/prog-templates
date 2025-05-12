using kis.DTO.Wh;
using kis.Service;
using Microsoft.AspNetCore.Mvc;

namespace kis.Controller
{
    [Route("api/warehouse")]
    [ApiController]
    public class WarehouseController : ControllerBase
    {
        private readonly WarehouseService service;
        public WarehouseController(WarehouseService service)
        {
            this.service = service;
        }

        // GET api/warehouse/get-history/5                             
        [HttpGet("get-history/{id}")]
        public async Task<IActionResult> getHistoryByProductId(long id)
        {
            var result = await service.getHistoryByProductId(id);
            return Ok(result);
        }

        // POST api/warehouse/get-by-date
        [HttpPost("get-by-date")]
        public async Task<IActionResult> findByDate([FromBody]DateTime date)
        {
            var (result,message) = await service.findByDate(date);
            if (message != "Ok")
                return NotFound(message);
            return Ok(result);
        }


        // GET api/warehouse/check-component
        [HttpGet("check-products")]
        public async Task<IActionResult> checkProducts()
        {
            var (result,message) = await service.checkProducts();
            if (message != "Ok")
                return BadRequest(message);
            return Ok(result);
        }

        // POST api/warehouse/change
        [HttpPost("change")]
        public async Task<IActionResult> change([FromBody] WarehouseChangeDto dto)
        {
            if (dto.ProductId <= 0)
                return BadRequest("id товара должен быть >= 0");
            if (dto.Number == 0)
                return BadRequest("Изменения не будут применены");
            var (result, message) = await service.change(dto);
            if (message != "Ok")
                return BadRequest(message);
            return Ok(result);
        }


        // DELETE api/warehouse/delete/5
        [HttpDelete("delete/{id}")]
        public async Task<IActionResult?> delete(int id)
        {
            if (id <= 0)
                return NotFound("id должен >= 0");

            var result = await service.delete(id);
            if (result == null) return NotFound("Не найдено");
            return Ok(result);
        }
    }
}
