using kis.Repository;
using kis.Service;
using Microsoft.AspNetCore.Builder;
using Microsoft.EntityFrameworkCore;

namespace kis
{
    public class Startup
    {
        public void ConfigureServices(IServiceCollection services)
        {
            var connectionString = "Server=localhost;Port=5432;Database=KIS;User Id=postgres;Password=rootroot;";
            services.AddDbContext<ApplicationContext>(options => options.UseNpgsql(connectionString));
            services.AddScoped<SpecRepository>();
            services.AddScoped<SpecService>();
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
