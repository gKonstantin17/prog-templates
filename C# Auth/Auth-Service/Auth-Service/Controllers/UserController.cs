using Auth_Service.Entity;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace Auth_Service.Controllers
{
    [ApiController]
    [Route("api/user")]
    [Authorize] // Требуется аутентификация
    public class UserController : ControllerBase
    {
        [HttpGet("/profile")]
        public IActionResult GetProfile()
        {
            var userId = User.FindFirst("UserId")?.Value;
            var userName = User.Identity?.Name;
            var userRole = User.FindFirst(ClaimTypes.Role)?.Value;

            return Ok(new
            {
                message = "Профиль пользователя",
                userId,
                userName,
                userRole
            });
        }
    }
}
