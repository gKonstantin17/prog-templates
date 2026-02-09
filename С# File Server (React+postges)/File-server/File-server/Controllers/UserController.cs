using File_server.Entity;
using File_server.Service;
using Microsoft.AspNetCore.Mvc;

namespace File_server.Controllers
{
    [Route("api/user")]
    [ApiController]
    public class UserController : ControllerBase
    {
        private UserService service;
        public UserController(UserService service)
        {
            this.service = service;
        }

        [HttpPost("/register")]
        public async Task<IActionResult> register(UserDto dto)
        {
            var result = await service.register(dto);
            return Ok(result);
        }

        [HttpPost("/login")]
        public async Task<IActionResult> login(UserDto dto)
        {
            var result = await service.login(dto);
            if (result == null) return BadRequest("Логин или пароль не верны");
            return Ok(result);
        }

        // Новый метод для получения пользователя по ID
        [HttpGet("/{id}")]
        public async Task<IActionResult> getUserById(long id)
        {
            try
            {
                var user = await service.findById(id);
                if (user == null) return NotFound("Пользователь не найден");

                // Не возвращаем пароль
                user.Password = null;
                return Ok(user);
            }
            catch (Exception ex)
            {
                return BadRequest(new { error = ex.Message });
            }
        }
    }
}
