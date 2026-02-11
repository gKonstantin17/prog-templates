namespace Auth_Service.Entity.DTO
{
    public class AuthResponseDto
    {
        public string Token { get; set; } = null!;
        public DateTime Expires { get; set; }
        public UserDto User { get; set; } = null!;
    }
}
