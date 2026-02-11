using Auth_Service.Repository;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using System.Text;

namespace Auth_Service
{
    public class Startup
    {
        public void ConfigureServices(IServiceCollection services)
        {
            // База данных
            var connectionString = "Server=localhost;Port=5432;Database=auth-sharp;User Id=postgres;Password=rootroot;";
            services.AddDbContext<ApplicationContext>(options => options.UseNpgsql(connectionString));

            // DI
            services.Scan(scan => scan
                .FromAssemblyOf<UserRepository>()
                .AddClasses(classes => classes.Where(type =>
                    type.Name.EndsWith("Repository") || type.Name.EndsWith("Service")))
                .AsSelf()
                .WithScopedLifetime());

            services.AddControllers();

            // Swagger
            services.AddSwaggerGen(c =>
            {
                c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
                {
                    Scheme = "bearer",
                    Type = SecuritySchemeType.Http
                });
                c.AddSecurityRequirement(new OpenApiSecurityRequirement
                {
                    { new OpenApiSecurityScheme
                        { Reference = new OpenApiReference { Type = ReferenceType.SecurityScheme, Id = "Bearer" } },
                        Array.Empty<string>() }
                });
            });

            // CORS
            services.AddCors(options => options.AddPolicy("AllowAll", builder =>
                builder.WithOrigins("http://localhost:3000", "https://localhost:3000")
                       .AllowAnyMethod()
                       .AllowAnyHeader()
                       .AllowCredentials()));

            // === Настройка JWT токена ===
            var jwtSettings = new JwtSettings
            {
                SecretKey = "your-very-long-secret-key-at-least-32-characters-here",
                Issuer = "AuthService",
                Audience = "AuthClient",
                ExpireMinutes = 60
            };

            // 1. Для AuthService (СОЗДАНИЕ токенов)
            services.Configure<JwtSettings>(options =>
            {
                options.SecretKey = jwtSettings.SecretKey;
                options.Issuer = jwtSettings.Issuer;
                options.Audience = jwtSettings.Audience;
                options.ExpireMinutes = jwtSettings.ExpireMinutes;
            });

            // 2. Для ASP.NET Core (ПРОВЕРКА токенов)
            services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
                .AddJwtBearer(options =>
                {
                    options.TokenValidationParameters = new TokenValidationParameters
                    {
                        ValidIssuer = jwtSettings.Issuer,
                        ValidAudience = jwtSettings.Audience,
                        IssuerSigningKey = new SymmetricSecurityKey(
                            Encoding.UTF8.GetBytes(jwtSettings.SecretKey)),
                        ValidateLifetime = true,
                        ClockSkew = TimeSpan.Zero
                    };
                    options.Events = new JwtBearerEvents
                    {
                        OnMessageReceived = context =>
                        {
                            context.Token = context.Request.Cookies["auth_token"];
                            return Task.CompletedTask;
                        }
                    };
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
            app.UseAuthentication();
            app.UseHttpsRedirection();
            app.UseRouting();
            app.UseAuthorization();
            app.UseEndpoints(endpoints => endpoints.MapControllers());
        }

        public class JwtSettings
        {
            public string SecretKey { get; set; } = string.Empty;
            public string Issuer { get; set; } = string.Empty;
            public string Audience { get; set; } = string.Empty;
            public int ExpireMinutes { get; set; }
        }
    }
}