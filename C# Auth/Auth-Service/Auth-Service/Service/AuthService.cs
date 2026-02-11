using Auth_Service.Entity;
using Auth_Service.Entity.DTO;
using Auth_Service.Repository;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using static Auth_Service.Startup;

namespace Auth_Service.Service
{
    public class AuthService
    {
        private readonly UserRepository _userRepository;
        private readonly JwtSettings _jwt;

        public AuthService(
            UserRepository userRepository,
            IOptions<JwtSettings> jwtOptions)
        {
            _userRepository = userRepository;
            _jwt = jwtOptions.Value;
        }

        public async Task<AuthResponseDto> Login(LoginDto loginDto)
        {
            var user = await _userRepository.GetByLogin(loginDto.Login);

            if (user == null || !BCrypt.Net.BCrypt.Verify(loginDto.Password, user.Password))
                throw new UnauthorizedAccessException("Неверный логин или пароль");

            return await GenerateAuthResponse(user);
        }

        public async Task<AuthResponseDto> Register(RegisterDto registerDto)
        {
            if (await _userRepository.LoginExists(registerDto.Login))
                throw new ArgumentException("Пользователь с таким логином уже существует");

            var user = new User
            {
                Name = registerDto.Name,
                Login = registerDto.Login,
                Password = BCrypt.Net.BCrypt.HashPassword(registerDto.Password),
                RoleId = 2
            };

            await _userRepository.Create(user);
            return await GenerateAuthResponse(user);
        }

        public async Task<bool> ValidateToken(string token)
        {
            try
            {
                var tokenHandler = new JwtSecurityTokenHandler();

                tokenHandler.ValidateToken(token, new TokenValidationParameters
                {
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = new SymmetricSecurityKey(
                        Encoding.UTF8.GetBytes(_jwt.SecretKey)),
                    ValidateIssuer = true,
                    ValidIssuer = _jwt.Issuer,
                    ValidateAudience = true,
                    ValidAudience = _jwt.Audience,
                    ValidateLifetime = true,
                    ClockSkew = TimeSpan.Zero
                }, out _);

                return true;
            }
            catch
            {
                return false;
            }
        }

        private async Task<AuthResponseDto> GenerateAuthResponse(User user)
        {
            var token = GenerateJwtToken(user);

            var fullUser = await _userRepository.GetById(user.Id);

            return new AuthResponseDto
            {
                Token = token,
                Expires = DateTime.UtcNow.AddMinutes(_jwt.ExpireMinutes),
                User = new UserDto
                {
                    Id = fullUser!.Id,
                    Name = fullUser.Name!,
                    Login = fullUser.Login!,
                    Role = fullUser.Role.Name!,
                    RoleId = fullUser.RoleId
                }
            };
        }

        private string GenerateJwtToken(User user)
        {
            var key = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(_jwt.SecretKey));

            var credentials = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            var claims = new List<Claim>
            {
                new Claim(ClaimTypes.NameIdentifier, user.Id.ToString()),
                new Claim(ClaimTypes.Name, user.Login!),
                new Claim(ClaimTypes.Role, user.Role?.Name ?? "User"),
                new Claim("UserId", user.Id.ToString()),
                new Claim("RoleId", user.RoleId.ToString())
            };

            var token = new JwtSecurityToken(
                issuer: _jwt.Issuer,
                audience: _jwt.Audience,
                claims: claims,
                expires: DateTime.UtcNow.AddMinutes(_jwt.ExpireMinutes),
                signingCredentials: credentials
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }
    }
}
