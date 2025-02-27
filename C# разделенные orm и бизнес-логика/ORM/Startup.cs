using Microsoft.AspNetCore.Builder;
using Microsoft.EntityFrameworkCore;
using ORM.Models;

namespace ORM
{
    public class Startup
    {
        public void ConfigureServices(IServiceCollection services)
        {
            var connectionString = "Server=localhost;Port=5436;Database=postgres;User Id=admin;Password=root;";
            services.AddDbContext<ApplicationContext>(options => options.UseNpgsql(connectionString));
            services.AddControllers();
            services.AddSwaggerGen();
        }

        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseSwagger();
                app.UseSwaggerUI();
            }
            app.UseHttpsRedirection();
            app.UseRouting();
            app.UseAuthorization();
            
            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }
    }
}
