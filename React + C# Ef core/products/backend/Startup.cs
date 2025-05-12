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

            // внедрение зависимостей через Scrtutor
            services.Scan(scan => scan
                .FromAssemblyOf<SpecRepository>() 
                .AddClasses(classes => classes.Where(type => type.Name.EndsWith("Repository") 
                || type.Name.EndsWith("Service"))) // Фильтрация классов по окончанию названия
                //.AsImplementedInterfaces() // внедрять через интерфейсы
                .AsSelf() // внедрять через классы
                .WithScopedLifetime() 
            );
            services.AddControllers();
            services.AddSwaggerGen();

            services.AddCors(options =>
            {
                options.AddPolicy("AllowAll", builder =>
                {
                    builder.AllowAnyOrigin()
                           .AllowAnyMethod()
                           .AllowAnyHeader();
                });
            });
        }

        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseSwagger();
                app.UseSwaggerUI();
            }

            app.UseCors("AllowAll");
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
