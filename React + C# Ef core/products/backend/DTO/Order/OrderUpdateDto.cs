namespace kis.DTO.Order
{
    public class OrderUpdateDto
    {
        public DateTime? CreateDate { get; set; }
        public DateTime? Deadline { get; set; }
        public int? Count { get; set; }
        public int? Price { get; set; }
        public bool? Status { get; set; }
        public long? Product_id { get; set; }
    }
}
