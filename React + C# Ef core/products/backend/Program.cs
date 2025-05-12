using kis;

public class Program
{
    public static void Main(string[] args)
    {
        CreateHostBuilder(args).Build().Run();
    }
    public static IHostBuilder CreateHostBuilder(string[] args)
    => Host.CreateDefaultBuilder(args)
        .ConfigureWebHostDefaults(
            webBuilder => webBuilder.UseStartup<Startup>()
            .UseUrls("http://localhost:5144", "https://localhost:7093")
            );

}