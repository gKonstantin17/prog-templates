using File_server;

public class Program
    // м€гкое удаление
    // проверить при удаление файла удал€етс€ ли фото профил€, не показывать в списке
{
    public static void Main(string[] args)
    {
        CreateHostBuilder(args).Build().Run();
    }
    public static IHostBuilder CreateHostBuilder(string[] args)
    => Host.CreateDefaultBuilder(args)
        .ConfigureWebHostDefaults(
            webBuilder => webBuilder.UseStartup<Startup>()
            .UseUrls("http://localhost:5144", "https://localhost:7093"));
}