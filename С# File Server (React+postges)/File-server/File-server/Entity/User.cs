using System.ComponentModel.DataAnnotations;

namespace File_server.Entity
{
    public class User
    {

        [Key]
        public long Id { get; set; }
        public string? Name { get; set; }
        public string? Login { get; set; }
        public string? Password { get; set; }
        public string? Photo { get; set; }
    }

    public class UserDto // данные для создания пользователя
    {
        public string? Name { get; set; }
        public string? Login { get; set; }
        public string? Password { get; set; }
    }
}
