using File_server.Entity;
using Microsoft.EntityFrameworkCore;

namespace File_server
{
    public class ApplicationContext : DbContext
    {
        public ApplicationContext() { }
        public ApplicationContext(DbContextOptions<ApplicationContext> options) : base(options) { }

        public DbSet<User> User { get; set; }
        public DbSet<FileData> FileData { get; set; }
    }

}

