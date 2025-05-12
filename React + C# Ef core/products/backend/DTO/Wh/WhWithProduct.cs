namespace kis.DTO.Wh
{
    public class WhWithProduct
    {
        public long Id { get; set; }
        public required long Product_Id { get; set; }
        public int Count { get; set; }
        public DateTime UpdateDate { get; set; }
        public string Product { get; set; }
    }
}
