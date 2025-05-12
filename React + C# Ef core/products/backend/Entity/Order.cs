using System.ComponentModel.DataAnnotations;

namespace kis.Entity
{
    public class Order
    {

        [Key]
        public long Id { get; set; }
        public DateTime CreateDate { get; set; }
        public DateTime Deadline { get; set; }
        public int Count { get; set; }
        public int Price { get; set; }
        public int TotalPrice { get; set; }
        public bool? Status { get; set; }
        public required Specification Product { get; set; }
    }
}
