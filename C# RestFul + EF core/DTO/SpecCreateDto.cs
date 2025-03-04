namespace kis.DTO
{
    public class SpecCreateDto
    {
        public required short Level { get; set; }
        public required string Name { get; set; }
        public required int Count { get; set; }
        public long? Parent_id { get; set; }
    }
}
