namespace kis.DTO.Order
{
    public class OrderWithProduct
    {
        public long? Id { get; set; }
        public DateTime? CreateDate { get; set; }
        public DateTime? Deadline { get; set; }
        public int? Count { get; set; }
        public int? Price { get; set; }
        public int? TotalPrice { get; set; }
        public bool? Status { get; set; }
        public long? Product_id { get; set; }
        public string? Product { get; set; }
    }
}
