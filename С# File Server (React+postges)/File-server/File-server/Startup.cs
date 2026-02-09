using File_server;
using File_server.Repository;
using Microsoft.EntityFrameworkCore;
using Scrutor;

namespace File_server
{
    public class Startup
    {
        private readonly IWebHostEnvironment _environment;

        public Startup(IWebHostEnvironment environment)
        {
            _environment = environment;
        }

        public void ConfigureServices(IServiceCollection services)
        {
            var connectionString = "Server=localhost;Port=5432;Database=file-service;User Id=postgres;Password=rootroot;";
            services.AddDbContext<ApplicationContext>(options => options.UseNpgsql(connectionString));

            // внедрение зависимостей через Scrutor
            services.Scan(scan => scan
               .FromAssemblyOf<FileRepository>()
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
            // Создаем необходимые директории при запуске приложения
            CreateUploadDirectories();
            app.UseStaticFiles();

            if (env.IsDevelopment())
            {
                app.UseSwagger();
                app.UseSwaggerUI();
            }
            app.UseHttpsRedirection();
            app.UseCors("AllowAll");
            app.UseRouting();
            app.UseAuthorization();

            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }

        private void CreateUploadDirectories()
        {
            // 1. Создаем папку wwwroot если она не существует
            var wwwrootPath = Path.Combine(_environment.ContentRootPath, "wwwroot");
            if (!Directory.Exists(wwwrootPath))
                Directory.CreateDirectory(wwwrootPath);

            // 2. Базовый путь - uploads внутри wwwroot
            var baseUploadPath = Path.Combine(wwwrootPath, "uploads");

            // 3. Создаем необходимые поддиректории
            var directories = new[]
            {
                Path.Combine(baseUploadPath, "files"),
                Path.Combine(baseUploadPath, "photos")
            };

            foreach (var directory in directories)
            {
                if (!Directory.Exists(directory))
                    Directory.CreateDirectory(directory);
            }
        }
    }
}