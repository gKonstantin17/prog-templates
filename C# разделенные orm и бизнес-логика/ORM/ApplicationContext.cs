using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Hosting;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Xml;
using Npgsql;
namespace ORM.Models
{
    public class ApplicationContext : DbContext
    {
        public ApplicationContext() {}
        public ApplicationContext(DbContextOptions<ApplicationContext> options) : base(options) {}
       
        // подключение сущности Client к приложению
        public DbSet<Client> Client { get; set; }
    }
}