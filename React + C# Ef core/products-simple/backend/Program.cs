using kis;
using System.Collections.Generic;
// у specification сделать truncate restart indentity
// 
/*
INSERT INTO public."Specification"("Level", "Name", "Count", "Parent_id") VALUES
-- Основной продукт - Косметичка (уровень 0)
(0, 'Косметичка', 1, NULL),

-- Основные компоненты (уровень 1)
(1, 'Ткань внешняя', 2, 1),
(1, 'Ткань подкладочная', 2, 1),
(1, 'Молния', 1, 1),
(1, 'Фурнитура', 1, 1),

-- Компоненты фурнитуры (уровень 2)
(2, 'Кольцо для подвеса', 1, 5),
(2, 'Декоративная нашивка', 1, 5),

-- Дополнительные материалы (уровень 1)
(1, 'Нитки', 1, 1),
(1, 'Утеплитель', 1, 1),
(1, 'Клей', 1, 1),

-- Компоненты декора (уровень 1)
(1, 'Декоративные элементы', 1, 1),
(2, 'Бисер', 10, 10),
(2, 'Стразы', 5, 10),
(2, 'Аппликация', 1, 10);
*/

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
            .UseUrls("http://0.0.0.0:5144") // слушать на всех IP
            );
}