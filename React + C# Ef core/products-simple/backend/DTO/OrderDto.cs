using kis.Entity;

namespace kis.DTO
{
    public class OrderDto
    {
        public long? Id { get; set; }
        public DateTime? CreateDate { get; set; }
        public DateTime? Deadline { get; set; }
        public int? Count { get; set; }
        public int? Price { get; set; }
        public int? FullPrice { get; set; }
        public long? Product_id { get; set; }
    }
}
