using Auth_Service.Entity;
using Auth_Service.Service;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace Auth_Service.Controllers
{
    
    [ApiController]
    [Route("api/auth")]
    public class AuthController : ControllerBase
    {
        private readonly AuthService _authService;

        public AuthController(AuthService authService)
        {
            _authService = authService;
        }

        [HttpPost("/login")]
        public async Task<IActionResult> Login([FromBody] LoginDto loginDto)
        {
            try
            {
                var response = await _authService.Login(loginDto);

                // Устанавливаем токен в куки
                Response.Cookies.Append("auth_token", response.Token, new CookieOptions
                {
                    HttpOnly = true,
                    Secure = true,
                    SameSite = SameSiteMode.Strict,
                    Expires = response.Expires
                });

                return Ok(new { message = "Вход выполнен успешно" });
            }
            catch (UnauthorizedAccessException ex)
            {
                return Unauthorized(new { message = ex.Message });
            }
        }

        [HttpPost("/register")]
        public async Task<IActionResult> Register([FromBody] RegisterDto registerDto)
        {
            try
            {
                var response = await _authService.Register(registerDto);

                // Устанавливаем токен в куки
                Response.Cookies.Append("auth_token", response.Token, new CookieOptions
                {
                    HttpOnly = true,
                    Secure = true,
                    SameSite = SameSiteMode.Strict,
                    Expires = response.Expires
                });

                return Ok(new { message = "Регистрация выполнена успешно" });
            }
            catch (ArgumentException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpPost("/logout")]
        [Authorize]
        public IActionResult Logout()
        {
            Response.Cookies.Delete("auth_token");
            return Ok(new { message = "Вы успешно вышли из системы" });
        }


        [HttpGet("/validate")]
        public async Task<IActionResult> ValidateToken()
        {
            var token = Request.Cookies["auth_token"];
            if (string.IsNullOrEmpty(token))
            {
                return Unauthorized(new { message = "Токен не найден" });
            }

            var isValid = await _authService.ValidateToken(token);
            if (!isValid)
            {
                return Unauthorized(new { message = "Недействительный токен" });
            }

            return Ok(new { valid = true });
        }

        [HttpGet("/me")]
        [Authorize]
        public IActionResult GetCurrentUser()
        {
            var user = new
            {
                Id = User.FindFirst("UserId")?.Value,
                Name = User.Identity?.Name,
                Role = User.FindFirst(ClaimTypes.Role)?.Value,
                RoleId = User.FindFirst("RoleId")?.Value
            };

            return Ok(user);
        }
    }
    
}
