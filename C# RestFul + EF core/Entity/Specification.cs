using System.ComponentModel.DataAnnotations;

namespace kis.Entity
{
    public class Specification
    {
        [Key]
        public long Id { get; set; }
        public required short Level { get; set; }
        public required string Name { get; set; }
        public required int Count { get; set; }
        public long? Parent_id { get; set; }
    }
}
