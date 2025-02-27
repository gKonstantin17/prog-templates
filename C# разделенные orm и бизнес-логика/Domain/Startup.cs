using Domain.Repositories;
using Microsoft.AspNetCore.Builder;
using Microsoft.EntityFrameworkCore;
namespace Domain
{
    public class Startup
    {


        public void ConfigureServices(IServiceCollection services)
        {

            var dalUrl = "https://127.0.0.1:7229/api";
            services.AddSingleton(dalUrl);
            HttpClientHandler clientHandler = new HttpClientHandler();
            clientHandler.ServerCertificateCustomValidationCallback = (sender, cert, chain, sslPolicyErrors) => { return true; };
            services.AddSingleton<HttpClientHandler>(clientHandler);

            services.AddControllers();

            services.AddSwaggerGen();
            services.AddCors(options =>
            {
                options.AddPolicy("AllowSpecificOrigin",
                    builder =>
                    {
                        
                        /*builder.AllowAnyOrigin() // для всех
                               .AllowAnyMethod()
                               .AllowAnyHeader();*/
                        builder.WithOrigins("http://localhost:5173")
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
                app.UseDeveloperExceptionPage();
            }
            else
            {
                app.UseExceptionHandler("/Error");
                app.UseHsts();
            }

            app.UseHttpsRedirection();
            app.UseRouting();
            app.UseAuthorization();
            app.UseCors("AllowSpecificOrigin"); // для получения запросов
            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }
    }
}

