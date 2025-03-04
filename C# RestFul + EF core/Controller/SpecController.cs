using kis.DTO;
using kis.Entity;
using kis.Service;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

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

        // GET: api/spec/get
        [HttpGet("/get")]
        public async Task<IActionResult> get()
        {
            var result = await service.get();
            return Ok(result);
        }

        // GET api/spec/find-by-id/5
        [HttpGet("/find-by-id/{id}")]
        public async Task<IActionResult> findById(int id)
        {
            var result = await service.findById(id);
            if (result == null)
                return NotFound(result);
            return Ok(result);
        }

        // GET api/spec/get-components/5
        [HttpGet("/get-components/{parent_id}")]
        public async Task<IActionResult> getComponentsById(int parent_id)
        {
            var result = await service.getComponentsById(parent_id);
            if (result.Count == 0) // коль-во элементов в списке
                return NotFound(result);
            return Ok(result);
        }
        [HttpGet("/find-part-of/{id}")]
        public async Task<IActionResult> findPartOf(int id)
        {
            var result = await service.findPartOf(id);
            return Ok(result);
        }

        // POST api/spec/create
        [HttpPost("/create")]
        public async Task<IActionResult> create([FromBody] SpecCreateDto spec)
        {
            var result = await service.create(spec);
            if (result == -1)
                return StatusCode(409,"Уровень должен быть меньше на 1");
            if (result == -2)
                return StatusCode(409,"Должно быть количество больше 0");
            return Ok(result);
        }


        // PUT api/spec/update/5
        [HttpPut("/update/{id}")]
        public async Task<IActionResult?> update(long id, [FromBody] SpecUpdateDto spec)
        {
            var result = await service.update(id, spec);
            return Ok(result);
        }

        // DELETE api/spec/delete/5
        [HttpDelete("/delete/{id}")]
        public async Task<IActionResult?> delete(int id)
        {
            var result = await service.delete(id);
            return Ok(result);
        }
    }
}
