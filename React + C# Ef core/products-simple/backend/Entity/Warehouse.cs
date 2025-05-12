using System.ComponentModel.DataAnnotations;

namespace kis.Entity
{
    public class Warehouse
    {
        [Key]
        public long Id { get; set; }
        public required Specification Product { get; set; }
        public int Count { get; set; }
        public DateTime UpdateDate { get; set; }

    }
}
