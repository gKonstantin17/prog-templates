using System.ComponentModel.DataAnnotations;
using System.Xml.Linq;

namespace File_server.Entity
{
    public class FileData
    {
        [Key]
        public long Id { get; set; }
        public string? Name { get; set; }
        public string? RealName{ get; set; }
        public string? Path { get; set; }
        public DateTime? UploadDate { get; set; }
        public string? Type { get; set; }
        public long? Size { get; set; } // в байтах, перевод Кб, Мб будет на фронтенде
        public bool? IsDeleted { get; set; }
    }

    public class FileDto
    {
        public string? Name { get; set; }
        public string? RealName { get; set; }
        public string? Path { get; set; }
        public DateTime? UploadDate { get; set; }
        public string? Type { get; set; }
        public long? Size { get; set; } // в байтах, перевод Кб, Мб будет на фронтенде
        public bool? IsDeleted { get; set; }
    }
}
