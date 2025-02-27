using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ORM.DTO.ClientDto;
using ORM.Models;


namespace ORM.Controllers
{
    [Route("api/clients")]
    [ApiController]
    public class ClientsController : ControllerBase
    {
        private readonly ApplicationContext _db;
        public ClientsController(ApplicationContext db)
        {
            _db = db;
        }
       
        // GET: api/clients
        [HttpGet]
        public async Task<IEnumerable<Client>> Get()
        {
            return await _db.Client.ToListAsync();
        }

        // GET api/clients/5
        [HttpGet("{id}")]
        public async Task<Client?> Get(int id)
        {
            var result = await _db.Client.SingleOrDefaultAsync(o => o.Id == id);
            if (result == null)
            {
                Response.StatusCode = StatusCodes.Status404NotFound;
                return default;
            }
            return result;
        }

        // POST api/clients
        [HttpPost]
        public async Task<IActionResult> Post([FromBody] Client value)
        {
            var result = await _db.Client.AddAsync(new Client
            {
                CreateDate = DateTime.UtcNow,
                UpdateDate = DateTime.UtcNow,
                Fullname = value.Fullname,
                Email = value.Email,
                Phone = value.Phone,
                Password = value.Password,

            }); ;
            await _db.SaveChangesAsync();
            return Ok(result.Entity.Id);
        }


        // PUT api/clients/5
        [HttpPut("{id}")]
        public async Task<IActionResult?> Put(int id, [FromBody] ClientDto value)
        {
            var client = await Get(id);
            if (client == null) return default;

            client.UpdateDate = DateTime.UtcNow;
            if (value.Fullname != null) client.Fullname = value.Fullname;
            if (value.Phone != null) client.Phone = value.Phone;
            if (value.Email != null) client.Email = value.Email;
            if (value.Password != null) client.Password = value.Password;
            var result = _db.Client.Update(client);
            await _db.SaveChangesAsync();
            return Ok(result.Entity.Id);
        }

        // DELETE api/clients/5
        [HttpDelete("{id}")]
        public async Task<IActionResult?> Delete(int id)
        {
            var client = await Get(id);
            if (client == null) return default;
            _db.Remove(client);
            await _db.SaveChangesAsync();
            return Ok(client.Id);
        }
    }
}
