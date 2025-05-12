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
        public int FullPrice { get; set; }
        public required Specification Product { get; set; }
    }
}
