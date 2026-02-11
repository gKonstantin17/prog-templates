using System.ComponentModel.DataAnnotations;

namespace Auth_Service.Entity
{
    public class Role
    {
        [Key]
        public long Id { get; set; }
        public string? Name { get; set; }
        public ICollection<User> Users { get; set; } = new List<User>();

    }
}
