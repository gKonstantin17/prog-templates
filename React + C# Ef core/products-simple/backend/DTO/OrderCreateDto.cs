using kis.Entity;

namespace kis.DTO
{
    public class OrderCreateDto
    {
        public DateTime? CreateDate { get; set; }
        public DateTime? Deadline { get; set; }
        public int? Count { get; set; }
        public int? Price { get; set; }
        public long? Product_id { get; set; }
    }
}
