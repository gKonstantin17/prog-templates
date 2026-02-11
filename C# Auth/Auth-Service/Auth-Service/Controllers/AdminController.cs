using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace Auth_Service.Controllers
{
    [ApiController]
    [Route("api/admin")]
    [Authorize(Roles = "Admin")]
    public class AdminController : ControllerBase
    {
        [HttpGet("/dashboard")]
        public IActionResult GetDashboard()
        {
            return Ok(new
            {
                message = "Добро пожаловать в админ-панель",
                data = "Секретные данные для админов"
            });
        }
    }
}
