using ORM.Models;
using System.ComponentModel.DataAnnotations;

namespace ORM.DTO.ClientDto
{
    public class ClientWithoutFKDto
    {
        public int Id { get; set; }
        public DateTime? UpdateDate { get; set; }
        public DateTime? CreateDate { get; set; }
        public string? Fullname { get; set; }
        public string? Phone { get; set; }
        public string? Email { get; set; }
        public string? Password { get; set; }

    }
}
