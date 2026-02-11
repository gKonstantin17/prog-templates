using Auth_Service.Entity;
using Microsoft.EntityFrameworkCore;

namespace Auth_Service
{
    public class ApplicationContext : DbContext
    {
        public ApplicationContext() { }

        public ApplicationContext(DbContextOptions<ApplicationContext> options) : base(options)
        {
            // Применяем миграции и инициализируем данные
            if (Database.CanConnect())
            {
                Database.Migrate();
                SeedData();
            }
            else
            {
                Database.EnsureCreated();
                SeedData();
            }
        }

        public DbSet<User> User { get; set; } = null!;
        public DbSet<Role> Role { get; set; } = null!;

        private void SeedData()
        {
            // Проверяем, есть ли уже роли
            if (!Role.Any())
            {
                Role.AddRange(
                    new Role { Id = 1, Name = "Admin" },
                    new Role { Id = 2, Name = "User" }
                );
                SaveChanges();
            }

            // Проверяем, есть ли уже пользователь admin
            if (!User.Any(u => u.Login == "admin"))
            {
                var adminRole = Role.First(r => r.Id == 1);

                User.Add(new User
                {
                    Name = "Администратор",
                    Login = "admin",
                    Password = BCrypt.Net.BCrypt.HashPassword("admin123"),
                    RoleId = adminRole.Id,
                    Role = adminRole
                });
                SaveChanges();
            }
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // Настройка связей
            modelBuilder.Entity<User>()
                .HasOne(u => u.Role)
                .WithMany(r => r.Users)
                .HasForeignKey(u => u.RoleId)
                .OnDelete(DeleteBehavior.Restrict);
        }
    }
}
