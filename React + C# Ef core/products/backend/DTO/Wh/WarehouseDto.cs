using kis.Entity;

namespace kis.DTO.Wh
{
    public class WarehouseDto
    {
        public long Id { get; set; }
        public required long Product_Id { get; set; }
        public int Count { get; set; }
        public DateTime UpdateDate { get; set; }
    }
}
