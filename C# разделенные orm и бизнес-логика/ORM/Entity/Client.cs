using System.ComponentModel.DataAnnotations;
namespace ORM.Models
{
    public class Client
    {
        [Key]
        public int id { get; set; }
        public DateTime updateDate { get; set; }
        public DateTime createDate { get; set; }

        public required string fullname { get; set; }
        public required string phone { get; set; }
        public required string email { get; set; }
        public required string password { get; set; }
        public Employee employee 
        public List<Order>? orders { get; set; }
    }
}