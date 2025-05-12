namespace kis.DTO.Wh
{
    public class WarehouseDeficitDto
    {
        public long ComponentId { get; set; }
        public string ComponentName { get; set; }
        public int Required { get; set; }
        public int Available { get; set; }
        public int Missing { get; set; }
        public string Status { get; set; }
    }
}
