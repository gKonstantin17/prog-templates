using kis.Entity;

namespace kis.DTO.Spec
{
    public class SpecDtoWithChildren
    {
        public long Id { get; set; }
        public short? Level { get; set; }
        public string? Name { get; set; }
        public int? Count { get; set; }
        public List<SpecDtoWithChildren>? Specifications { get; set; }

    }
}
