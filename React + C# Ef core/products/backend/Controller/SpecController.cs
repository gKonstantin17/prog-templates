using kis.DTO.Spec;
using kis.Service;
using Microsoft.AspNetCore.Mvc;

namespace kis.Controller
{
    [Route("api/spec")]
    [ApiController]
    public class SpecController : ControllerBase
    {
        private readonly SpecService service;
        public SpecController(SpecService service)
        {
            this.service = service;
        }

        // GET api/spec/get-all-hierarchy
        [HttpGet("get-all-hierarchy")]
        public async Task<IActionResult> getAllHierarchy()
        {
            var result = await service.getAllHierarchy();
            return Ok(result);
        }
      
        [HttpPost("count-components")]
        public async Task<IActionResult> countComponents([FromBody] int id)
        {
            if (id <= 0)
                return NotFound("id должен >= 0");

            var result = await service.countComponentsById(id);
            if (!result.Specifications.Any())
                return NotFound(result);

            // нижний элемент, который с "specifications": []
            return Ok(result);
        }

        // POST api/spec/create
        [HttpPost("create")]
        public async Task<IActionResult> create([FromBody] SpecDto spec)
        {
            // проверка required полей
            if (spec.Level == null)
                return StatusCode(403, "Уровень должен быть указан");
            if (spec.Level < 1)
                return StatusCode(403, "Уровень должен быть >= 1");
            if (spec.Name == null)
                return StatusCode(403, "Название должно быть указано");
            if (spec.Count == null)
                return StatusCode(403, "Количество должно быть указано");

            // проверка полей по внутренней логике и создание записи
            var (result,message) = await service.create(spec);
            if (message != "Ok")
                return BadRequest(message);
            return Ok(result);
        }


        // PUT api/spec/update/5
        [HttpPut("update/{id}")]
        public async Task<IActionResult?> update(long id, [FromBody] SpecDto spec)
        {
            if (id <= 0)
                return NotFound("id должен >= 0");

            // можно изменять любое поле
            var result = await service.update(id, spec);
            return Ok(result);
        }

        // DELETE api/spec/delete/5
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
