using kis.Entity;
using Microsoft.EntityFrameworkCore;

namespace kis
{
    public class ApplicationContext : DbContext
    {
        public ApplicationContext() { }
        public ApplicationContext(DbContextOptions<ApplicationContext> options) : base(options) { }

        public DbSet<Specification> Specification { get; set; }
    }
}
