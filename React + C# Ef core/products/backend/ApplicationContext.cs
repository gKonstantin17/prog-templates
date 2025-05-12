using kis.Entity;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using System.Collections.Generic;


// cd[Название проекта]
// dotnet ef migrations add InitialCreate
// dotnet ef database update
namespace kis
{
    public class ApplicationContext : DbContext
    {
        public ApplicationContext() { }
        public ApplicationContext(DbContextOptions<ApplicationContext> options) : base(options) { }

        public DbSet<Specification> Specification { get; set; }
        public DbSet<Order> Order { get; set; }
        public DbSet<Warehouse> Warehouse { get; set; }


        protected override void ConfigureConventions(ModelConfigurationBuilder configurationBuilder)
        { // по дефолту DateTime = timestamp with time zone
            configurationBuilder.Properties<DateTime>()
                .HaveColumnType("timestamp without time zone")
                // Конвертер в локальное время т.к. запросы делаются в utc формате
                .HaveConversion<DateTimeToTimestampConverter>(); 
        }
    }

    internal class DateTimeToTimestampConverter : ValueConverter<DateTime, DateTime>
    {
        public DateTimeToTimestampConverter()
            : base(
                v => DateTime.SpecifyKind(v, DateTimeKind.Unspecified),
                v => DateTime.SpecifyKind(v, DateTimeKind.Unspecified))
        {
        }
    }
}
// sql запрос с датой в postgre
/*
 INSERT INTO public."Warehouse"(
 "ProductId", "Count", "UpdateDate")
    VALUES(1, 23, '2025-04-13 22:22:42.625');
*/
