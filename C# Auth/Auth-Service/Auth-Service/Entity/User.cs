using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Auth_Service.Entity
{
    public class User
    {
        [Key]
        public long Id { get; set; }
        public string? Name { get; set; }
        public string? Login { get; set; }
        public string? Password { get; set; }

        [ForeignKey("Role")]
        public long RoleId { get; set; }
        public Role Role { get; set; }

        // Конструктор без параметров для EF Core
        public User() { }

        // Конструктор для удобного создания
        public User(string name, string login, string password, long roleId)
        {
            Name = name;
            Login = login;
            Password = HashPassword(password);
            RoleId = roleId;
        }

        private static string HashPassword(string password)
        {
            // Используем BCrypt для хеширования
            return BCrypt.Net.BCrypt.HashPassword(password);
        }
    }

    public class LoginDto
    {
        public string Login { get; set; } = null!;
        public string Password { get; set; } = null!;
    }
    public class RegisterDto
    {
        public string Name { get; set; } = null!;
        public string Login { get; set; } = null!;
        public string Password { get; set; } = null!;
    }
    public class UserDto
    {
        public long Id { get; set; }
        public string Name { get; set; } = null!;
        public string Login { get; set; } = null!;
        public string Role { get; set; } = null!;
        public long RoleId { get; set; }
    }
}
